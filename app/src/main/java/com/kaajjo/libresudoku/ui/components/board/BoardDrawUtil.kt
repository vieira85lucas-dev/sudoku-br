package com.kaajjo.libresudoku.ui.components.board

import android.graphics.Paint
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.kaajjo.libresudoku.core.Cell
import com.kaajjo.libresudoku.core.Note
import kotlin.math.floor
import kotlin.math.sqrt

fun DrawScope.drawRoundCell(
    row: Int,
    col: Int,
    gameSize: Int,
    rect: Rect,
    color: Color,
    cornerRadius: CornerRadius = CornerRadius.Zero
) {
    val path = Path().apply {
        addRoundRect(
            roundRectForCell(
                row = row,
                col = col,
                gameSize = gameSize,
                rect = rect,
                cornerRadius = cornerRadius
            )
        )
    }
    drawPath(
        path = path,
        color = color
    )
}

fun DrawScope.drawNotes(
    size: Int,
    paint: Paint,
    highlightPaint: Paint,
    notes: List<Note>,
    notesToHighlight: List<Note>,
    cellSize: Float,
    killerSumBounds: android.graphics.Rect,
) {
    val padding = cellSize * NOTE_PADDING_RATIO
    // No modo Assassino, a soma da gaiola ocupa o topo da celula.
    val topReserve = if (killerSumBounds.height() > 0) killerSumBounds.height() * 1.5f else 0f

    // O tamanho do texto vem do quadrinho, para o numero sempre caber dentro da celula.
    val referenceSlot = noteSlot(1, size, cellSize, padding, topReserve)
    paint.textSize = noteTextSizeFor(referenceSlot)
    val digitBounds = android.graphics.Rect()
    paint.getTextBounds("8", 0, 1, digitBounds)
    val digitHeight = digitBounds.height().toFloat()

    drawIntoCanvas { canvas ->
        notes.forEach { note ->
            val slot = noteSlot(note.value, size, cellSize, padding, topReserve)
            val centerX = note.col * cellSize + slot.centerX
            val centerY = note.row * cellSize + slot.centerY

            if (notesToHighlight.contains(note)) {
                // Quadradinho de fundo atras da anotacao, como nos numeros destacados.
                val half = minOf(slot.width, slot.height) * 0.5f * 0.98f
                canvas.nativeCanvas.drawRoundRect(
                    android.graphics.RectF(centerX - half, centerY - half, centerX + half, centerY + half),
                    half * 0.3f,
                    half * 0.3f,
                    highlightPaint
                )
            }

            val text = note.value.toString(16).uppercase()
            canvas.nativeCanvas.drawText(
                text,
                centerX - paint.measureText(text) / 2f,
                centerY + digitHeight / 2f,
                paint
            )
        }
    }
}

/** Folga entre a grade de anotacoes e a borda da celula. */
private const val NOTE_PADDING_RATIO = 0.035f

fun DrawScope.drawNumbers(
    size: Int,
    board: List<List<Cell>>,
    highlightErrors: Boolean,
    errorTextPaint: Paint,
    lockedTextPaint: Paint,
    textPaint: Paint,
    questions: Boolean,
    cellSize: Float
) {
    drawIntoCanvas { canvas ->
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (board[i][j].value != 0) {
                    val paint = when {
                        board[i][j].error && highlightErrors -> errorTextPaint
                        board[i][j].locked -> lockedTextPaint
                        else -> textPaint
                    }

                    val textToDraw =
                        if (questions) "?" else board[i][j].value.toString(16).uppercase()
                    val textBounds = android.graphics.Rect()
                    textPaint.getTextBounds(textToDraw, 0, 1, textBounds)
                    val textWidth = paint.measureText(textToDraw)

                    canvas.nativeCanvas.drawText(
                        textToDraw,
                        board[i][j].col * cellSize + (cellSize - textWidth) / 2f,
                        board[i][j].row * cellSize + (cellSize + textBounds.height()) / 2f,
                        paint
                    )
                }
            }
        }
    }
}

fun DrawScope.drawBoardFrame(
    thickLineColor: Color,
    thickLineWidth: Float,
    maxWidth: Float,
    cornerRadius: CornerRadius
) {
    drawRoundRect(
        color = thickLineColor,
        topLeft = Offset.Zero,
        size = Size(maxWidth, maxWidth),
        cornerRadius = cornerRadius,
        style = Stroke(width = thickLineWidth)
    )
}

fun roundRectForCell(
    row: Int,
    col: Int,
    gameSize: Int,
    rect: Rect,
    cornerRadius: CornerRadius
): RoundRect {
    val topLeft = if (row == 0 && col == 0) cornerRadius else CornerRadius.Zero
    val topRight = if (row == 0 && col == gameSize - 1) cornerRadius else CornerRadius.Zero
    val bottomLeft = if (row == gameSize - 1 && col == 0) cornerRadius else CornerRadius.Zero
    val bottomRight =
        if (row == gameSize - 1 && col == gameSize - 1) cornerRadius else CornerRadius.Zero

    return RoundRect(
        rect = rect,
        topLeft = topLeft,
        topRight = topRight,
        bottomLeft = bottomLeft,
        bottomRight = bottomRight
    )
}

fun DrawScope.drawCrossSelection(
    gameSize: Int,
    sectionHeight: Int,
    sectionWidth: Int,
    color: Color,
    cellSize: Float
) {
    for (i in 0 until gameSize / sectionWidth) {
        for (j in 0 until gameSize / sectionHeight) {
            if ((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)) {
                drawRect(
                    color = color,
                    topLeft = Offset(
                        x = i * sectionWidth * cellSize,
                        y = j * sectionHeight * cellSize
                    ),
                    size = Size(cellSize * sectionWidth, cellSize * sectionHeight)
                )
            }
        }
    }
}

