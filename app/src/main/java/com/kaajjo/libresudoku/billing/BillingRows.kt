package com.kaajjo.libresudoku.billing

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import com.kaajjo.libresudoku.R
import com.kaajjo.libresudoku.ads.LocalAdsManager
import com.kaajjo.libresudoku.ui.theme.NeoColors
import com.kaajjo.libresudoku.ui.theme.NeoMenuRow
import com.kaajjo.libresudoku.ui.util.findActivity

private const val SUBSCRIPTIONS_URL = "https://play.google.com/store/account/subscriptions"

/** Linhas do menu "Mais" para remover os anuncios, restaurar a compra e gerenciar a assinatura. */
@Composable
fun BillingRows() {
    val billing = LocalBillingManager.current ?: return
    val adsManager = LocalAdsManager.current ?: return
    val adsRemoved by adsManager.adsRemoved.collectAsState()
    val price by billing.price.collectAsState()
    val activity = LocalContext.current.findActivity()
    val uriHandler = LocalUriHandler.current

    if (adsRemoved) {
        NeoMenuRow(
            title = stringResource(R.string.ads_removed_title),
            subtitle = stringResource(R.string.ads_removed_manage),
            painter = rememberVectorPainter(Icons.Outlined.Block),
            iconColor = NeoColors.Yellow,
            cardColor = NeoColors.Mint,
            onClick = { uriHandler.openUri(SUBSCRIPTIONS_URL) }
        )
        return
    }

    val formattedPrice = price
    if (formattedPrice != null && activity != null) {
        NeoMenuRow(
            title = stringResource(R.string.remove_ads_title),
            subtitle = stringResource(R.string.remove_ads_price, formattedPrice),
            painter = rememberVectorPainter(Icons.Outlined.Block),
            iconColor = NeoColors.Yellow,
            cardColor = NeoColors.Mint,
            onClick = { billing.purchase(activity) }
        )
    }
    NeoMenuRow(
        title = stringResource(R.string.restore_purchase),
        painter = rememberVectorPainter(Icons.Outlined.Restore),
        iconColor = NeoColors.Lavender,
        onClick = { billing.restorePurchases() }
    )
}
