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

/**
 * Anotacoes que ganham destaque: as do numero escolhido no teclado (modo "valor primeiro")
 * ou, se nao houver, as do numero da celula selecionada (modo "celula primeiro").
 */
fun notesToHighlight(
    notes: List<com.kaajjo.libresudoku.core.Note>,
    digitFirstNumber: Int,
    board: List<List<Cell>>,
    selected: Cell
): List<com.kaajjo.libresudoku.core.Note> {
    val digit = when {
        digitFirstNumber > 0 -> digitFirstNumber
        selected.row in board.indices && selected.col in board[selected.row].indices ->
            board[selected.row][selected.col].value
        else -> 0
    }
    return if (digit > 0) notes.filter { it.value == digit } else emptyList()
}
