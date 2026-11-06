package com.kaajjo.libresudoku.ui.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaajjo.libresudoku.R
import com.kaajjo.libresudoku.ads.PrivacyOptionsRow
import com.kaajjo.libresudoku.billing.BillingRows
import com.kaajjo.libresudoku.destinations.AboutScreenDestination
import com.kaajjo.libresudoku.destinations.BackupScreenDestination
import com.kaajjo.libresudoku.destinations.FoldersScreenDestination
import com.kaajjo.libresudoku.destinations.HowToPlayScreenDestination
import com.kaajjo.libresudoku.destinations.LearnScreenDestination
import com.kaajjo.libresudoku.destinations.SettingsCategoriesScreenDestination
import com.kaajjo.libresudoku.ui.components.AnimatedNavigation
import com.kaajjo.libresudoku.ui.progress.LevelCard
import com.kaajjo.libresudoku.ui.progress.ProgressViewModel
import com.kaajjo.libresudoku.ui.theme.NeoColors
import com.kaajjo.libresudoku.ui.theme.NeoMenuRow
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(style = AnimatedNavigation::class)
@Composable
fun MoreScreen(
    navigator: DestinationsNavigator,
    progressViewModel: ProgressViewModel = hiltViewModel()
) {
    val level = progressViewModel.level.collectAsState().value
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.more_title_a))
                    withStyle(SpanStyle(color = NeoColors.LavenderDeep)) {
                        append(stringResource(R.string.more_title_b))
                    }
                },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = stringResource(R.string.more_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LevelCard(level)

            BillingRows()

            NeoMenuRow(
                title = stringResource(R.string.how_to_play_title),
                painter = painterResource(R.drawable.ic_outline_help_outline_24),
                iconColor = NeoColors.Yellow,
                onClick = { navigator.navigate(HowToPlayScreenDestination()) }
            )
            NeoMenuRow(
                title = stringResource(R.string.settings_title),
                painter = painterResource(R.drawable.ic_settings_24),
                iconColor = NeoColors.Lavender,
                onClick = { navigator.navigate(SettingsCategoriesScreenDestination()) }
            )
            NeoMenuRow(
                title = stringResource(R.string.backup_restore_title),
                painter = rememberVectorPainter(image = Icons.Rounded.SettingsBackupRestore),
                iconColor = NeoColors.Mint,
                onClick = { navigator.navigate(BackupScreenDestination()) }
            )
            NeoMenuRow(
                title = stringResource(R.string.title_folders),
                painter = rememberVectorPainter(Icons.Outlined.Folder),
                iconColor = NeoColors.Coral,
                onClick = { navigator.navigate(FoldersScreenDestination()) }
            )
            NeoMenuRow(
                title = stringResource(R.string.learn_screen_title),
                painter = painterResource(R.drawable.ic_outline_help_outline_24),
                iconColor = NeoColors.Mint,
                onClick = { navigator.navigate(LearnScreenDestination()) }
            )
            NeoMenuRow(
                title = stringResource(R.string.about_title),
                painter = painterResource(R.drawable.ic_outline_info_24),
                iconColor = NeoColors.Lavender,
                onClick = { navigator.navigate(AboutScreenDestination()) }
            )
            PrivacyOptionsRow()
        }
    }
}
