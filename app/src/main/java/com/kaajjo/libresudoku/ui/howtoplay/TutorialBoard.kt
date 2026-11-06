package com.kaajjo.libresudoku.ui.howtoplay

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp

/** Puzzle de exemplo (0 = vazio). Solução conhecida, usada nas animações do tutorial. */
internal val TutorialPuzzle = intArrayOf(
    5, 3, 0, 0, 7, 0, 0, 0, 0,
    6, 0, 0, 1, 9, 5, 0, 0, 0,
    0, 9, 8, 0, 0, 0, 0, 6, 0,
    8, 0, 0, 0, 6, 0, 0, 0, 3,
    4, 0, 0, 8, 0, 3, 0, 0, 1,
    7, 0, 0, 0, 2, 0, 0, 0, 6,
    0, 6, 0, 0, 0, 0, 2, 8, 0,
    0, 0, 0, 4, 1, 9, 0, 0, 5,
    0, 0, 0, 0, 8, 0, 0, 7, 9
)

internal fun cellIndex(row: Int, col: Int) = row * 9 + col

internal fun rowCells(row: Int) = (0..8).map { cellIndex(row, it) }.toSet()

internal fun colCells(col: Int) = (0..8).map { cellIndex(it, col) }.toSet()

internal fun boxCells(row: Int, col: Int): Set<Int> {
    val r0 = row / 3 * 3
    val c0 = col / 3 * 3
    return (0..2).flatMap { dr -> (0..2).map { dc -> cellIndex(r0 + dr, c0 + dc) } }.toSet()
}

/** Tudo que o tabuleiro de demonstração precisa desenhar num instante da animação. */
internal data class TutorialBoardState(
    /** Números colocados pelo jogador (índice da célula -> valor). */
    val userValues: Map<Int, Int> = emptyMap(),
    /** Anotações (índice da célula -> candidatos). */
    val notes: Map<Int, Set<Int>> = emptyMap(),
    val selected: Int? = null,
    /** Células destacadas como linha/coluna/bloco. */
    val houses: Set<Int> = emptySet(),
    /** Todas as células com este número ficam destacadas (0 = nenhum). */
    val digit: Int = 0,
    /** Célula onde o "toque" pulsa (null = nenhuma). */
    val pulse: Int? = null
)

@Composable
internal fun TutorialBoard(
    state: TutorialBoardState,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val thin = with(density) { 1.dp.toPx() }
    val thick = with(density) { 3.dp.toPx() }

    val transition = rememberInfiniteTransition(label = "pulse")
    val pulseProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "pulseProgress"
    )

    val houseColor = colors.primary.copy(alpha = 0.14f)
    val selectedColor = colors.primary.copy(alpha = 0.40f)
    val digitColor = colors.primary.copy(alpha = 0.28f)
    val givenText = colors.onSurface
    val userText = colors.primary
    val noteText = colors.onSurfaceVariant
    val thinLine = colors.outlineVariant
    val thickLine = colors.onSurface
    val surface = colors.surface
    val primary = colors.primary

    // Valor mostrado em cada célula (dado do puzzle ou colocado pelo jogador).
    val shown = remember(state.userValues) {
        IntArray(81) { state.userValues[it] ?: TutorialPuzzle[it] }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val cell = size.width / 9f
        drawRect(surface, size = size)

        // Fundo das células
        for (i in 0 until 81) {
            val topLeft = Offset((i % 9) * cell, (i / 9) * cell)
            val cellSize = Size(cell, cell)
            if (i in state.houses) drawRect(houseColor, topLeft, cellSize)
            if (state.digit != 0 && shown[i] == state.digit) drawRect(digitColor, topLeft, cellSize)
            if (i == state.selected) drawRect(selectedColor, topLeft, cellSize)
        }

        // Linhas finas e grossas (blocos 3x3 mais escuros e mais grossos)
        for (k in 0..9) {
            if (k % 3 != 0) {
                drawLine(thinLine, Offset(k * cell, 0f), Offset(k * cell, size.height), thin)
                drawLine(thinLine, Offset(0f, k * cell), Offset(size.width, k * cell), thin)
            }
        }
        for (k in 0..9 step 3) {
            drawLine(thickLine, Offset(k * cell, 0f), Offset(k * cell, size.height), thick)
            drawLine(thickLine, Offset(0f, k * cell), Offset(size.width, k * cell), thick)
        }

        // Números e anotações
        val bigStyle = TextStyle(fontSize = (cell * 0.55f).toSp(), fontWeight = FontWeight.Medium)
        val noteStyle = TextStyle(fontSize = (cell * 0.27f).toSp(), fontWeight = FontWeight.Medium)
        for (i in 0 until 81) {
            val r = i / 9
            val c = i % 9
            val value = shown[i]
            if (value != 0) {
                val given = TutorialPuzzle[i] != 0
                val layout = textMeasurer.measure(
                    value.toString(),
                    bigStyle.copy(color = if (given) givenText else userText)
                )
                drawText(
                    layout,
                    topLeft = Offset(
                        c * cell + (cell - layout.size.width) / 2f,
                        r * cell + (cell - layout.size.height) / 2f
                    )
                )
            } else {
                state.notes[i]?.forEach { n ->
                    val layout = textMeasurer.measure(n.toString(), noteStyle.copy(color = noteText))
                    val sub = cell / 3f
                    drawText(
                        layout,
                        topLeft = Offset(
                            c * cell + ((n - 1) % 3) * sub + (sub - layout.size.width) / 2f,
                            r * cell + ((n - 1) / 3) * sub + (sub - layout.size.height) / 2f
                        )
                    )
                }
            }
        }

        // Toque pulsando
        state.pulse?.let { i ->
            val center = Offset((i % 9 + 0.5f) * cell, (i / 9 + 0.5f) * cell)
            drawCircle(
                color = primary.copy(alpha = 0.7f * (1f - pulseProgress)),
                radius = cell * (0.25f + 0.65f * pulseProgress),
                center = center,
                style = Stroke(width = thick)
            )
            drawCircle(primary.copy(alpha = 0.55f), radius = cell * 0.16f, center = center)
        }
    }
}
