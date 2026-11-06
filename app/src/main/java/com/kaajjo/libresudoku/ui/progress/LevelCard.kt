package com.kaajjo.libresudoku.ui.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaajjo.libresudoku.R
import com.kaajjo.libresudoku.core.progress.LevelInfo
import com.kaajjo.libresudoku.ui.theme.NeoCard
import com.kaajjo.libresudoku.ui.theme.NeoColors

/** Nome do nivel (a lista acaba em "Lenda"; niveis acima repetem o ultimo nome). */
@Composable
fun levelName(level: Int): String {
    val names = stringArrayResource(R.array.level_names)
    return names[(level - 1).coerceIn(0, names.lastIndex)]
}

/** Cartao de progresso: anel com o nivel, nome, pontos que faltam e barra. */
@Composable
fun LevelCard(info: LevelInfo, modifier: Modifier = Modifier) {
    NeoCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        shadow = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val track = MaterialTheme.colorScheme.surfaceVariant
            Box(Modifier.size(78.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(78.dp)) {
                    val stroke = 9.dp.toPx()
                    val inset = stroke / 2f
                    val arc = Size(size.width - stroke, size.height - stroke)
                    drawArc(track, 0f, 360f, false, Offset(inset, inset), arc, style = Stroke(stroke))
                    drawArc(
                        NeoColors.Lavender, -90f, 360f * info.progress.coerceAtLeast(0.02f), false,
                        Offset(inset, inset), arc, style = Stroke(stroke, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = stringResource(R.string.level_short, info.number),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(levelName(info.number), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(
                    text = "${info.totalPoints} ${stringResource(R.string.vp_unit)} · " +
                            stringResource(R.string.level_to_next, info.pointsToNext, info.number + 1),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), CircleShape)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(info.progress.coerceAtLeast(0.03f))
                            .height(10.dp)
                            .background(NeoColors.Lavender)
                    )
                }
            }
        }
    }
}
