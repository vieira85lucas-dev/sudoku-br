package com.kaajjo.libresudoku.core.progress

import com.kaajjo.libresudoku.core.qqwing.GameDifficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PointsTest {
    @Test
    fun basePointsByDifficulty() {
        assertEquals(50, Points.forWin(GameDifficulty.Easy, 0))
        assertEquals(100, Points.forWin(GameDifficulty.Moderate, 0))
        assertEquals(200, Points.forWin(GameDifficulty.Hard, 0))
        assertEquals(400, Points.forWin(GameDifficulty.Challenge, 0))
    }

    @Test
    fun eachMistakeCostsTenPercent() {
        assertEquals(180, Points.forWin(GameDifficulty.Hard, 1))
        assertEquals(160, Points.forWin(GameDifficulty.Hard, 2))
    }

    @Test
    fun mistakesNeverTakeMoreThanHalf() {
        assertEquals(100, Points.forWin(GameDifficulty.Hard, 5))
        assertEquals(100, Points.forWin(GameDifficulty.Hard, 50))
    }

    @Test
    fun negativeMistakesAreIgnored() {
        assertEquals(50, Points.forWin(GameDifficulty.Easy, -3))
    }

    @Test
    fun levelStartsAtOne() {
        val info = Levels.of(0)
        assertEquals(1, info.number)
        assertEquals(0, info.intoLevel)
        assertEquals(150, info.levelSpan)
        assertEquals(0f, info.progress, 0.0001f)
    }

    @Test
    fun levelChangesAtThresholds() {
        assertEquals(1, Levels.of(149).number)
        assertEquals(2, Levels.of(150).number)
        assertEquals(2, Levels.of(599).number)
        assertEquals(3, Levels.of(600).number)
    }

    @Test
    fun progressInsideLevel() {
        val info = Levels.of(375) // nivel 2 vai de 150 a 600 -> 225 de 450
        assertEquals(2, info.number)
        assertEquals(225, info.intoLevel)
        assertEquals(450, info.levelSpan)
        assertEquals(0.5f, info.progress, 0.0001f)
        assertEquals(225, info.pointsToNext)
    }

    @Test
    fun thresholdsGrow() {
        for (n in 1..20) assertTrue(Levels.thresholdFor(n + 1) > Levels.thresholdFor(n))
    }
}
