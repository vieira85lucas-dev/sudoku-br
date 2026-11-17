package com.kaajjo.libresudoku.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeProgressTest {
    private val solved = "5346789127"       // 10 casas
    private val initial = "5300000000"      // 2 casas dadas

    @Test
    fun `given cells and correct entries count`() {
        // 2 dadas + 3 certas (posicoes 2,3,4) = 5 de 10
        assertEquals(50, filledPercentCorrect("5346700000", initial, solved))
    }

    @Test
    fun `wrong entries do not count`() {
        // 2 dadas + 1 certa (posicao 2) + 2 erradas (posicoes 3 e 4) = 3 de 10
        assertEquals(30, filledPercentCorrect("5341100000", initial, solved))
    }

    @Test
    fun `an empty progress counts only the given cells`() {
        assertEquals(20, filledPercentCorrect(initial, initial, solved))
    }

    @Test
    fun `a missing solution falls back to counting every filled cell`() {
        val current = "5341100000" // 5 preenchidas
        assertEquals(50, filledPercentCorrect(current, initial, null))
        assertEquals(50, filledPercentCorrect(current, null, solved))
    }

    @Test
    fun `a solution that does not match the board falls back to counting every filled cell`() {
        val current = "5341100000"
        assertEquals(50, filledPercentCorrect(current, initial, "534"))
        assertEquals(50, filledPercentCorrect(current, initial, "5340789127"))
    }

    @Test
    fun `letters of bigger boards are compared ignoring case`() {
        // tabuleiros 12x12 usam letras (a, b, c) para 10, 11 e 12
        assertEquals(100, filledPercentCorrect("ab", "00", "AB"))
    }

    @Test
    fun `an empty string is zero`() {
        assertEquals(0, filledPercentCorrect("", null, null))
    }
}
