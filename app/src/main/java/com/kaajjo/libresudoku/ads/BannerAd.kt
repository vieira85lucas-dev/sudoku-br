package com.kaajjo.libresudoku.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.kaajjo.libresudoku.BuildConfig

/** Fornece o [AdsManager] para qualquer tela sem passar pelo ViewModel. Nulo em previews e testes. */
val LocalAdsManager = staticCompositionLocalOf<AdsManager?> { null }

/** Banner adaptativo fixo. Nao ocupa espaco quando os anuncios estao desligados. */
@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val adsManager = LocalAdsManager.current ?: return
    val enabled by adsManager.adsEnabled.collectAsState()
    if (!enabled) return

    val context = LocalContext.current
    val widthDp = LocalConfiguration.current.screenWidthDp
    val adSize = remember(widthDp) {
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)
    }
    val adView = remember(adSize) {
        AdView(context).apply {
            setAdSize(adSize)
            adUnitId = BuildConfig.ADMOB_BANNER_ID
            loadAd(AdRequest.Builder().build())
        }
    }
    DisposableEffect(adView) {
        onDispose { adView.destroy() }
    }
    Box(modifier = modifier.fillMaxWidth().navigationBarsPadding()) {
        AndroidView(factory = { adView })
    }
}

/** Verdadeiro quando o usuario pagou para remover os anuncios. */
@Composable
fun adsRemovedState(): Boolean {
    val adsManager = LocalAdsManager.current ?: return false
    val removed by adsManager.adsRemoved.collectAsState()
    return removed
}

/** Verdadeiro quando ha um video premiado pronto para exibir. */
@Composable
fun rewardedReadyState(): Boolean {
    val adsManager = LocalAdsManager.current ?: return false
    val ready by adsManager.rewardedReady.collectAsState()
    return ready
}
