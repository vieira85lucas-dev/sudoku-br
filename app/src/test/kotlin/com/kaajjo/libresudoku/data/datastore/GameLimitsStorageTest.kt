package com.kaajjo.libresudoku.data.datastore

import org.junit.Assert.assertEquals
import org.junit.Test

class GameLimitsStorageTest {
    @Test
    fun `limits survive an encode and decode round trip`() {
        val limits = GameLimits(hintsUsed = 2, secondChancesUsed = 3)
        assertEquals(limits, DataStoreGameLimitsStorage.decode(DataStoreGameLimitsStorage.encode(limits)))
    }

    @Test
    fun `unreadable values become zero`() {
        assertEquals(GameLimits(), DataStoreGameLimitsStorage.decode(""))
        assertEquals(GameLimits(), DataStoreGameLimitsStorage.decode("abc,xyz"))
        assertEquals(GameLimits(hintsUsed = 1, secondChancesUsed = 0), DataStoreGameLimitsStorage.decode("1"))
    }

    @Test
    fun `negative values are never restored`() {
        assertEquals(GameLimits(), DataStoreGameLimitsStorage.decode("-4,-1"))
    }
}
