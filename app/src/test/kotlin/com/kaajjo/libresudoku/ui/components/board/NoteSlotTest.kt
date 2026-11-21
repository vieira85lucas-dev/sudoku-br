package com.kaajjo.libresudoku.ui.components.board

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteSlotTest {
    private val cell = 100f
    private val pad = 3.5f

    @Test
    fun `grids have room for every number`() {
        assertEquals(3, noteGridColumns(9)); assertEquals(3, noteGridRows(9))
        assertEquals(3, noteGridColumns(6)); assertEquals(2, noteGridRows(6))
        assertEquals(4, noteGridColumns(12)); assertEquals(3, noteGridRows(12))
    }

    @Test
    fun `every slot is inside the cell and no two slots share a spot`() {
        listOf(6, 9, 12).forEach { size ->
            val slots = (1..size).map { noteSlot(it, size, cell, pad, topReserve = 0f) }
            slots.forEach { s ->
                assertTrue(s.centerX - s.width / 2f >= pad - 0.01f)
                assertTrue(s.centerX + s.width / 2f <= cell - pad + 0.01f)
                assertTrue(s.centerY - s.height / 2f >= pad - 0.01f)
                assertTrue(s.centerY + s.height / 2f <= cell - pad + 0.01f)
            }
            assertEquals(size, slots.map { it.centerX to it.centerY }.toSet().size)
        }
    }

    @Test
    fun `number one is at the top left and the last number at the bottom right`() {
        val first = noteSlot(1, 9, cell, pad, 0f)
        val last = noteSlot(9, 9, cell, pad, 0f)
        assertTrue(first.centerX < cell / 2 && first.centerY < cell / 2)
        assertTrue(last.centerX > cell / 2 && last.centerY > cell / 2)
    }

    @Test
    fun `notes read left to right in rows`() {
        val one = noteSlot(1, 9, cell, pad, 0f)
        val two = noteSlot(2, 9, cell, pad, 0f)
        val four = noteSlot(4, 9, cell, pad, 0f)
        assertTrue(two.centerX > one.centerX && two.centerY == one.centerY)
        assertTrue(four.centerY > one.centerY && four.centerX == one.centerX)
    }

    @Test
    fun `the text always fits inside its slot`() {
        listOf(6, 9, 12).forEach { size ->
            val slot = noteSlot(1, size, cell, pad, 0f)
            val textSize = noteTextSizeFor(slot)
            assertTrue(textSize * 0.72f <= slot.height + 0.01f)
            assertTrue(textSize * 0.62f <= slot.width + 0.01f)
        }
    }

    @Test
    fun `a top reserve for the killer sum pushes the grid down and keeps it inside the cell`() {
        val plain = noteSlot(1, 9, cell, pad, 0f)
        val reserved = noteSlot(1, 9, cell, pad, topReserve = 20f)
        assertTrue(reserved.centerY > plain.centerY)
        val bottom = noteSlot(9, 9, cell, pad, topReserve = 20f)
        assertTrue(bottom.centerY + bottom.height / 2f <= cell - pad + 0.01f)
    }

    @Test
    fun `the text is bigger on 12x12 than before and still fits`() {
        val slot12 = noteSlot(1, 12, cell, pad, 0f)
        assertTrue(noteTextSizeFor(slot12) > cell / 4f * 0.8f)
    }
}
