package com.kaajjo.libresudoku.ui.game

import com.kaajjo.libresudoku.core.Cell
import org.junit.Assert.assertEquals
import org.junit.Test

class GameProgressTest {
    private fun board(values: List<Int>, errors: Set<Int> = emptySet()): List<List<Cell>> =
        List(9) { r -> List(9) { c -> Cell(r, c, values[r * 9 + c], error = (r * 9 + c) in errors) } }

    @Test
    fun emptyBoardIsZeroPercent() {
        assertEquals(0, filledPercent(board(List(81) { 0 })))
    }

    @Test
    fun filledCellsCount() {
        // 27 de 81 casas preenchidas = 33%
        assertEquals(33, filledPercent(board(List(81) { if (it < 27) 5 else 0 })))
    }

    @Test
    fun errorCellsDoNotCount() {
        val values = List(81) { if (it < 27) 5 else 0 }
        // 3 das 27 casas estao erradas: 24 de 81 = 29%
        assertEquals(29, filledPercent(board(values, errors = setOf(0, 1, 2))))
    }

    @Test
    fun fullBoardWithNoErrorsIsHundred() {
        assertEquals(100, filledPercent(board(List(81) { 1 })))
    }

    @Test
    fun emptyListIsZero() {
        assertEquals(0, filledPercent(emptyList()))
    }
}
