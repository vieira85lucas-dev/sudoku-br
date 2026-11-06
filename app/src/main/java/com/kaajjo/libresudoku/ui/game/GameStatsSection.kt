package com.kaajjo.libresudoku.ui.game

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Grade
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaajjo.libresudoku.R
import com.kaajjo.libresudoku.core.PreferencesConstants
import com.kaajjo.libresudoku.core.progress.Points
import com.kaajjo.libresudoku.core.qqwing.GameDifficulty
import com.kaajjo.libresudoku.core.qqwing.GameType
import com.kaajjo.libresudoku.core.utils.toFormattedString
import com.kaajjo.libresudoku.data.database.model.Record
import com.kaajjo.libresudoku.ui.theme.NeoCard
import com.kaajjo.libresudoku.ui.theme.NeoColors
import kotlin.time.toKotlinDuration

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AfterGameStats(
    difficulty: GameDifficulty,
    type: GameType,
    hintsUsed: Int,
    mistakesMade: Int,
    mistakesLimit: Boolean,
    mistakesLimitCount: Int,
    giveUp: Boolean,
    notesTaken: Int,
    records: List<Record>,
    timeText: String,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        if (giveUp) {
            DefeatHeader(
                text = if (mistakesLimit && mistakesLimitCount >= PreferencesConstants.MISTAKES_LIMIT) {
                    stringResource(R.string.saved_game_mistakes_limit)
                } else {
                    stringResource(R.string.saved_game_give_up)
                }
            )
        } else {
            VictoryHeader(
                difficulty = difficulty,
                mistakes = mistakesLimitCount,
                hintsUsed = hintsUsed,
                timeText = timeText
            )
        }

        Spacer(Modifier.height(12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!giveUp && records.isNotEmpty()) {
                StatBoxWithBottomPadding(
                    text = {
                        Text(
                            text = stringResource(
                                R.string.stat_time_average,
                                DateUtils.formatElapsedTime(records.sumOf { it.time.seconds } / records.count())
                            )
                        )
                    }
                )
                StatBoxWithBottomPadding(
                    text = {
                        Text(
                            text = stringResource(
                                R.string.stat_time_best,
                                records.first().time
                                    .toKotlinDuration()
                                    .toFormattedString()
                            )
                        )
                    }
                )
            }
            StatBoxWithBottomPadding(
                text = { Text("${stringResource(difficulty.resName)} ${stringResource(type.resName)}") },
                icon = { Icon(Icons.Rounded.Grade, contentDescription = null) }
            )
            StatBoxWithBottomPadding(
                text = { Text(stringResource(R.string.mistakes_made, mistakesMade)) },
                icon = { Icon(Icons.Rounded.Cancel, contentDescription = null) }
            )
            StatBoxWithBottomPadding(
                text = { Text(stringResource(R.string.notes_taken, notesTaken)) },
                icon = { Icon(Icons.Rounded.Edit, contentDescription = null) }
            )
        }
    }
}

/** Festa de vitoria: "Parabens!", pontos VP ganhos e tempo, dicas e nivel da partida. */
@Composable
private fun VictoryHeader(
    difficulty: GameDifficulty,
    mistakes: Int,
    hintsUsed: Int,
    timeText: String
) {
    val points = Points.forWin(difficulty, mistakes)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeoCard(color = NeoColors.Yellow, shape = CircleShape, shadow = 3.dp) {
            Icon(
                imageVector = Icons.Rounded.Celebration,
                contentDescription = null,
                tint = NeoColors.Charcoal,
                modifier = Modifier
                    .padding(12.dp)
                    .size(30.dp)
            )
        }
        Column(Modifier.padding(start = 14.dp)) {
            Text(
                text = stringResource(R.string.victory_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = stringResource(R.string.victory_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Spacer(Modifier.height(14.dp))
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        shadow = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.vp_earned).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = NeoColors.LavenderDeep
            )
            Text(
                text = "+$points ${stringResource(R.string.vp_unit)}",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                VictoryFact(Icons.Rounded.Timer, timeText, stringResource(R.string.victory_time))
                VictoryFact(Icons.Rounded.Lightbulb, hintsUsed.toString(), stringResource(R.string.victory_hints))
                VictoryFact(Icons.Rounded.Bolt, stringResource(difficulty.resName), stringResource(R.string.victory_level))
            }
        }
    }
}

@Composable
private fun VictoryFact(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = NeoColors.LavenderDeep)
        Text(value, fontWeight = FontWeight.ExtraBold)
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DefeatHeader(text: String) {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        color = NeoColors.Coral,
        shape = RoundedCornerShape(24.dp),
        shadow = 3.dp
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(
                text = stringResource(R.string.defeat_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = NeoColors.Charcoal
            )
            Text(text = text, color = NeoColors.Charcoal)
        }
    }
}

@Composable
fun StatBox(
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = { }
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            text()
        }
    }
}

// TODO: Remove this when cross-axis arrangement support is added to FlowRow
// https://android-review.googlesource.com/c/platform/frameworks/support/+/2478295
@Composable
fun StatBoxWithBottomPadding(
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = { }
) {
    StatBox(
        text = text,
        icon = icon,
        modifier = modifier.padding(bottom = 8.dp)
    )
}