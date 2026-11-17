package com.kaajjo.libresudoku.data.datastore

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DataStoreGameLimitsStorageTest {
    private val storage = DataStoreGameLimitsStorage(ApplicationProvider.getApplicationContext<Context>())

    @Test
    fun `an unknown game starts at zero`() = runBlocking {
        assertEquals(GameLimits(), storage.get(999L))
    }

    @Test
    fun `saved limits are read back and games do not mix`() = runBlocking {
        storage.save(1L, GameLimits(hintsUsed = 2, secondChancesUsed = 1))
        storage.save(2L, GameLimits(hintsUsed = 3, secondChancesUsed = 4))
        assertEquals(GameLimits(2, 1), storage.get(1L))
        assertEquals(GameLimits(3, 4), storage.get(2L))
        storage.save(1L, GameLimits(3, 1))
        assertEquals(GameLimits(3, 1), storage.get(1L))
    }
}
