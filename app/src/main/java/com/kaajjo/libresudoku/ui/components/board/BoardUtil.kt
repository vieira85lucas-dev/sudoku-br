package com.kaajjo.libresudoku.ui.components.board

import com.kaajjo.libresudoku.core.qqwing.GameType

fun getNoteColumnNumber(number: Int, size: Int): Int {
    if (size == 9 || size == 6) {
        return when (number) {
            1, 2, 3 -> 0
            4, 5, 6 -> 1
            7, 8, 9 -> 2
            else -> 0
        }
    } else if (size == 12) {
        return when (number) {
            1, 2, 3, 4 -> 0
            5, 6, 7, 8 -> 1
            9, 10, 11, 12 -> 2
            else -> 0
        }
    }
    return 0
}

fun getNoteRowNumber(number: Int, size: Int): Int {
    if (size == 9 || size == 6) {
        return when (number) {
            1, 4, 7 -> 0
            2, 5, 8 -> 1
            3, 6, 9 -> 2
            else -> 0
        }
    } else if (size == 12) {
        return when (number) {
            1, 5, 9 -> 0
            2, 6, 10 -> 1
            3, 7, 11 -> 2
            4, 8, 12 -> 3
            else -> 0
        }
    }
    return 0
}

fun getSectionHeightForSize(size: Int): Int {
    return when (size) {
        6 -> GameType.Default6x6.sectionHeight
        9 -> GameType.Default9x9.sectionHeight
        12 -> GameType.Default12x12.sectionHeight
        else -> GameType.Default9x9.sectionHeight
    }
}

fun getSectionWidthForSize(size: Int): Int {
    return when (size) {
        6 -> GameType.Default6x6.sectionWidth
        9 -> GameType.Default9x9.sectionWidth
        12 -> GameType.Default12x12.sectionWidth
        else -> GameType.Default9x9.sectionWidth
    }
}

/** Quadrinho de uma anotacao dentro da celula (medidas em pixels, relativas ao canto da celula). */
data class NoteSlot(val centerX: Float, val centerY: Float, val width: Float, val height: Float)

/** Colunas da grade de anotacoes dentro da celula: 3 (6x6 e 9x9) ou 4 (12x12). */
fun noteGridColumns(size: Int): Int = when (size) {
    12 -> 4
    else -> 3
}

/** Linhas da grade de anotacoes: 2 (6x6) ou 3 (9x9 e 12x12). */
fun noteGridRows(size: Int): Int {
    val columns = noteGridColumns(size)
    return (size + columns - 1) / columns
}

/**
 * Quadrinho do numero [value] na celula de lado [cellSize]. A grade ocupa a celula inteira,
 * com uma folga fina ([padding]) nas bordas; [topReserve] guarda espaco no topo (soma do modo Assassino).
 * O numero 1 fica no canto superior esquerdo e os seguintes seguem em linhas, da esquerda para a direita.
 */
fun noteSlot(value: Int, size: Int, cellSize: Float, padding: Float, topReserve: Float): NoteSlot {
    val columns = noteGridColumns(size)
    val rows = noteGridRows(size)
    val index = (value - 1).coerceIn(0, size - 1)
    val gridLeft = padding
    val gridTop = maxOf(padding, topReserve)
    val width = (cellSize - 2 * padding) / columns
    val height = (cellSize - gridTop - padding) / rows
    val col = index % columns
    val row = index / columns
    return NoteSlot(
        centerX = gridLeft + col * width + width / 2f,
        centerY = gridTop + row * height + height / 2f,
        width = width,
        height = height
    )
}

/** Tamanho do texto (px) para o numero caber no quadrinho: ~72% da altura do texto e ~62% da largura sao o digito. */
fun noteTextSizeFor(slot: NoteSlot): Float =
    minOf(slot.height * 0.92f / 0.72f, slot.width * 0.96f / 0.62f)
