package com.kaajjo.libresudoku.ads

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.kaajjo.libresudoku.R
import com.kaajjo.libresudoku.ui.components.PreferenceRow
import com.kaajjo.libresudoku.ui.util.findActivity

/** Linha do menu que reabre o formulario de privacidade. So aparece quando o Google exige a opcao. */
@Composable
fun PrivacyOptionsRow() {
    val adsManager = LocalAdsManager.current ?: return
    val required by adsManager.privacyOptionsRequired.collectAsState()
    if (!required) return
    val activity = LocalContext.current.findActivity() ?: return

    PreferenceRow(
        title = stringResource(R.string.about_privacy_title),
        painter = rememberVectorPainter(Icons.Outlined.PrivacyTip),
        onClick = { adsManager.showPrivacyOptions(activity) }
    )
}
