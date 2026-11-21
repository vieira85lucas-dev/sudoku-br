package com.kaajjo.libresudoku.ui.game

import com.kaajjo.libresudoku.core.Cell
import com.kaajjo.libresudoku.core.Note
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLayoutTest {
    @Test
    fun `content taller than the free space turns the compact layout on`() {
        assertTrue(needsCompactLayout(contentHeightPx = 1300, availableHeightPx = 1200))
    }

    @Test
    fun `content that fits keeps the normal layout`() {
        assertFalse(needsCompactLayout(contentHeightPx = 1200, availableHeightPx = 1200))
        assertFalse(needsCompactLayout(contentHeightPx = 900, availableHeightPx = 1200))
    }

    @Test
    fun `an unknown free space never turns the compact layout on`() {
        assertFalse(needsCompactLayout(contentHeightPx = 1300, availableHeightPx = 0))
    }

    private fun board(selectedValue: Int) = List(9) { r ->
        List(9) { c -> Cell(r, c, if (r == 0 && c == 0) selectedValue else 0) }
    }

    private val notes = listOf(Note(1, 1, 3), Note(1, 2, 3), Note(1, 2, 5), Note(2, 2, 7))

    @Test
    fun `notes of the digit chosen on the keypad are highlighted in digit first mode`() {
        val result = notesToHighlight(notes, digitFirstNumber = 5, board = board(0), selected = Cell(-1, -1))
        assertEquals(listOf(Note(1, 2, 5)), result)
    }

    @Test
    fun `notes of the selected cell's digit are highlighted in cell first mode`() {
        val result = notesToHighlight(notes, digitFirstNumber = 0, board = board(3), selected = Cell(0, 0))
        assertEquals(listOf(Note(1, 1, 3), Note(1, 2, 3)), result)
    }

    @Test
    fun `an empty selected cell highlights nothing`() {
        assertTrue(notesToHighlight(notes, digitFirstNumber = 0, board = board(0), selected = Cell(0, 0)).isEmpty())
    }

    @Test
    fun `no selection highlights nothing`() {
        assertTrue(notesToHighlight(notes, digitFirstNumber = 0, board = board(3), selected = Cell(-1, -1)).isEmpty())
    }

    @Test
    fun `the keypad digit wins over the selected cell`() {
        val result = notesToHighlight(notes, digitFirstNumber = 7, board = board(3), selected = Cell(0, 0))
        assertEquals(listOf(Note(2, 2, 7)), result)
    }
}
