package com.kaajjo.libresudoku.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.staticCompositionLocalOf
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.kaajjo.libresudoku.ads.AdsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Fornece o [BillingManager] as telas. Nulo em previews e testes. */
val LocalBillingManager = staticCompositionLocalOf<BillingManager?> { null }

/**
 * Assinatura que remove os anuncios (Google Play Billing).
 * O produto `remove_ads_weekly` precisa existir na Play Console com o mesmo ID.
 * Sem servidor proprio, a validacao da compra e feita so no aparelho.
 */
@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val adsManager: AdsManager
) : PurchasesUpdatedListener {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _price = MutableStateFlow<String?>(null)

    /** Preco formatado na moeda do usuario, ou nulo enquanto nao carregou. */
    val price: StateFlow<String?> = _price.asStateFlow()

    private var productDetails: ProductDetails? = null

    private val client: BillingClient = BillingClient.newBuilder(appContext)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .enableAutoServiceReconnection()
        .build()

    init {
        // Ultimo estado conhecido, para nao mostrar anuncios a quem ja pagou enquanto a Play responde.
        adsManager.setAdsRemoved(prefs.getBoolean(KEY_ADS_REMOVED, false))
    }

    /** Conecta a Play e atualiza o preco e o estado da assinatura. Pode ser chamado varias vezes. */
    fun connect() {
        if (client.isReady) {
            scope.launch { refresh() }
            return
        }
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    scope.launch { refresh() }
                } else {
                    Log.w(TAG, "Billing setup: ${result.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() = Unit
        })
    }

    /** Consulta de novo o preco e as compras. Tambem serve para "Restaurar compra". */
    suspend fun refresh() {
        loadProduct()
        refreshPurchases()
    }

    fun restorePurchases() {
        scope.launch { refreshPurchases() }
    }

    /** Abre a tela de compra do Google. Nao faz nada se o preco ainda nao carregou. */
    fun purchase(activity: Activity) {
        val details = productDetails ?: return
        val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: return
        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .setOfferToken(offerToken)
            .build()
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .build()
        client.launchBillingFlow(activity, flowParams)
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            scope.launch { handlePurchases(purchases) }
        } else if (result.responseCode != BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.w(TAG, "Purchase update: ${result.responseCode} ${result.debugMessage}")
        }
    }

    private suspend fun loadProduct() {
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(PRODUCT_ID)
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        val result = client.queryProductDetails(
            QueryProductDetailsParams.newBuilder().setProductList(listOf(product)).build()
        )
        if (result.billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.w(TAG, "Product details: ${result.billingResult.debugMessage}")
            return
        }
        val details = result.productDetailsList?.firstOrNull { it.productId == PRODUCT_ID } ?: return
        productDetails = details
        _price.value = details.subscriptionOfferDetails
            ?.firstOrNull()
            ?.pricingPhases
            ?.pricingPhaseList
            ?.firstOrNull()
            ?.formattedPrice
    }

    private suspend fun refreshPurchases() {
        val result = client.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        )
        if (result.billingResult.responseCode != BillingClient.BillingResponseCode.OK) return
        handlePurchases(result.purchasesList)
    }

    private suspend fun handlePurchases(purchases: List<Purchase>) {
        val active = purchases.filter {
            it.purchaseState == Purchase.PurchaseState.PURCHASED && PRODUCT_ID in it.products
        }
        active.filterNot { it.isAcknowledged }.forEach { purchase ->
            val ack = client.acknowledgePurchase(
                AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
            )
            if (ack.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.w(TAG, "Acknowledge: ${ack.debugMessage}")
            }
        }
        val premium = active.isNotEmpty()
        prefs.edit().putBoolean(KEY_ADS_REMOVED, premium).apply()
        adsManager.setAdsRemoved(premium)
    }

    companion object {
        const val PRODUCT_ID = "remove_ads_weekly"
        private const val TAG = "BillingManager"
        private const val PREFS_NAME = "billing"
        private const val KEY_ADS_REMOVED = "ads_removed"
    }
}
