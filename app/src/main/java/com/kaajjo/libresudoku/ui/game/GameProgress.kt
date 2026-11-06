package com.kaajjo.libresudoku.ui.game

import com.kaajjo.libresudoku.core.Cell

/**
 * Percentual do tabuleiro preenchido. As casas dadas contam; as casas com erro
 * (numero errado) nao contam, para o percentual so subir com acertos.
 */
fun filledPercent(board: List<List<Cell>>): Int {
    val total = board.sumOf { it.size }
    if (total == 0) return 0
    val filled = board.sumOf { row -> row.count { it.value != 0 && !it.error } }
    return filled * 100 / total
}
