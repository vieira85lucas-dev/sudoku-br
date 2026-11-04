package com.kaajjo.libresudoku.ads

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.kaajjo.libresudoku.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

/**
 * Centraliza consentimento (UMP), inicializacao do SDK e os tres formatos de anuncio.
 * Nada e carregado antes de [gatherConsent] liberar `canRequestAds`.
 */
@Singleton
class AdsManager @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(appContext)
    private val sdkInitialized = AtomicBoolean(false)

    private val _canRequestAds = MutableStateFlow(false)
    private val _adsRemoved = MutableStateFlow(false)
    private val _privacyOptionsRequired = MutableStateFlow(false)
    private val _rewardedReady = MutableStateFlow(false)

    /** Verdadeiro quando o usuario pagou para remover os anuncios. */
    val adsRemoved: StateFlow<Boolean> = _adsRemoved.asStateFlow()

    /** Verdadeiro quando ha um video premiado carregado e pronto para exibir. */
    val rewardedReady: StateFlow<Boolean> = _rewardedReady.asStateFlow()

    /** Verdadeiro quando o app precisa oferecer o botao "Opcoes de privacidade". */
    val privacyOptionsRequired: StateFlow<Boolean> = _privacyOptionsRequired.asStateFlow()

    /** Anuncios podem ser exibidos: consentimento resolvido e plano sem anuncios nao ativo. */
    val adsEnabled: StateFlow<Boolean> = combine(_canRequestAds, _adsRemoved) { can, removed -> can && !removed }
        .stateIn(scope, SharingStarted.Eagerly, false)

    private var interstitial: InterstitialAd? = null
    private var interstitialLoading = false
    private var rewarded: RewardedAd? = null
        set(value) {
            field = value
            _rewardedReady.value = value != null
        }
    private var rewardedLoading = false
    private var lastInterstitialShownAt = 0L
    private var gamesSinceInterstitial = 0

    fun setAdsRemoved(removed: Boolean) {
        _adsRemoved.value = removed
        if (removed) {
            interstitial = null
            rewarded = null
        }
    }

    /** Executa o fluxo de consentimento do Google (UMP) e, se permitido, inicializa o SDK. */
    fun gatherConsent(activity: Activity) {
        val params = ConsentRequestParameters.Builder().build()
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) Log.w(TAG, "Consent form: ${formError.message}")
                    onConsentResolved()
                }
            },
            { error ->
                Log.w(TAG, "Consent info update: ${error.message}")
                // Sem resposta do UMP, usa o ultimo estado conhecido (pode ja permitir anuncios).
                onConsentResolved()
            }
        )
        // Estado de consentimento de sessoes anteriores ja permite iniciar o SDK sem esperar a rede.
        if (consentInformation.canRequestAds()) onConsentResolved()
    }

    /** Reabre o formulario de privacidade para o usuario rever a escolha. */
    fun showPrivacyOptions(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            if (formError != null) Log.w(TAG, "Privacy options: ${formError.message}")
            onConsentResolved()
        }
    }

    private fun onConsentResolved() {
        _privacyOptionsRequired.value =
            consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        if (!consentInformation.canRequestAds()) return
        _canRequestAds.value = true
        if (sdkInitialized.compareAndSet(false, true)) {
            MobileAds.initialize(appContext) {
                scope.launch { preloadFullScreenAds() }
            }
        }
    }

    private fun preloadFullScreenAds() {
        loadInterstitial()
        loadRewarded()
    }

    private fun loadInterstitial() {
        if (!_canRequestAds.value || _adsRemoved.value || interstitial != null || interstitialLoading) return
        interstitialLoading = true
        InterstitialAd.load(
            appContext,
            BuildConfig.ADMOB_INTERSTITIAL_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitial = ad
                    interstitialLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitial = null
                    interstitialLoading = false
                }
            }
        )
    }

    /**
     * Conta uma partida concluida e mostra um intersticial se passaram [GAMES_BETWEEN_INTERSTITIALS]
     * partidas e [MIN_INTERSTITIAL_INTERVAL_MS] desde o ultimo. [onFinished] sempre e chamado.
     */
    fun onGameFinished(activity: Activity, onFinished: () -> Unit = {}) {
        gamesSinceInterstitial++
        val ad = interstitial
        val elapsed = SystemClock.elapsedRealtime() - lastInterstitialShownAt
        val due = gamesSinceInterstitial >= GAMES_BETWEEN_INTERSTITIALS &&
            (lastInterstitialShownAt == 0L || elapsed >= MIN_INTERSTITIAL_INTERVAL_MS)
        if (!adsEnabled.value || ad == null || !due) {
            loadInterstitial()
            onFinished()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitial = null
                loadInterstitial()
                onFinished()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitial = null
                loadInterstitial()
                onFinished()
            }
        }
        gamesSinceInterstitial = 0
        lastInterstitialShownAt = SystemClock.elapsedRealtime()
        ad.show(activity)
    }

    private fun loadRewarded() {
        if (!_canRequestAds.value || _adsRemoved.value || rewarded != null || rewardedLoading) return
        rewardedLoading = true
        RewardedAd.load(
            appContext,
            BuildConfig.ADMOB_REWARDED_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewarded = ad
                    rewardedLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewarded = null
                    rewardedLoading = false
                }
            }
        )
    }

    /** Ha um video premiado pronto para exibir. */
    fun isRewardedReady(): Boolean = adsEnabled.value && rewarded != null

    /**
     * Mostra o video premiado. [onReward] so roda se o usuario assistiu o suficiente.
     * Se nao houver video disponivel, chama [onUnavailable].
     */
    fun showRewarded(activity: Activity, onReward: () -> Unit, onUnavailable: () -> Unit) {
        val ad = rewarded
        if (!adsEnabled.value || ad == null) {
            loadRewarded()
            onUnavailable()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewarded = null
                loadRewarded()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                rewarded = null
                loadRewarded()
                onUnavailable()
            }
        }
        ad.show(activity) { onReward() }
    }

    private companion object {
        const val TAG = "AdsManager"
        const val GAMES_BETWEEN_INTERSTITIALS = 2
        const val MIN_INTERSTITIAL_INTERVAL_MS = 2 * 60 * 1000L
    }
}
