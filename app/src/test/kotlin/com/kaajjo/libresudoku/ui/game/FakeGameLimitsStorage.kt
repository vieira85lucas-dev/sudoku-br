package com.kaajjo.libresudoku.ui.game

import com.kaajjo.libresudoku.data.datastore.GameLimits
import com.kaajjo.libresudoku.data.datastore.GameLimitsStorage

/** Guarda os limites em memoria, como o aparelho faria entre uma abertura do app e outra. */
class FakeGameLimitsStorage : GameLimitsStorage {
    val stored = mutableMapOf<Long, GameLimits>()
    var saveCount = 0
        private set

    override suspend fun get(gameUid: Long): GameLimits = stored[gameUid] ?: GameLimits()

    override suspend fun save(gameUid: Long, limits: GameLimits) {
        stored[gameUid] = limits
        saveCount++
    }
}
