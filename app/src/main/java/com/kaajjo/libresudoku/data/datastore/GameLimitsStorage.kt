package com.kaajjo.libresudoku.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

/** Quantas dicas e segundas chances ja foram usadas em uma partida. */
data class GameLimits(
    val hintsUsed: Int = 0,
    val secondChancesUsed: Int = 0
)

/**
 * Guarda no aparelho os limites usados por partida. Sem isso, fechar e reabrir o app
 * zerava os contadores e o jogador ganhava dicas e segundas chances de novo.
 */
interface GameLimitsStorage {
    suspend fun get(gameUid: Long): GameLimits
    suspend fun save(gameUid: Long, limits: GameLimits)
}

class DataStoreGameLimitsStorage(context: Context) : GameLimitsStorage {
    private val Context.limitsDataStore: DataStore<Preferences> by preferencesDataStore(name = "game_limits")
    private val dataStore = context.limitsDataStore

    private fun key(gameUid: Long) = stringPreferencesKey("game_$gameUid")

    override suspend fun get(gameUid: Long): GameLimits {
        val raw = dataStore.data.first()[key(gameUid)] ?: return GameLimits()
        return decode(raw)
    }

    override suspend fun save(gameUid: Long, limits: GameLimits) {
        dataStore.edit { it[key(gameUid)] = encode(limits) }
    }

    internal companion object {
        fun encode(limits: GameLimits) = "${limits.hintsUsed},${limits.secondChancesUsed}"

        /** Valores ilegiveis viram zero, e nunca negativos. */
        fun decode(raw: String): GameLimits {
            val parts = raw.split(',')
            val hints = parts.getOrNull(0)?.toIntOrNull()?.coerceAtLeast(0) ?: 0
            val chances = parts.getOrNull(1)?.toIntOrNull()?.coerceAtLeast(0) ?: 0
            return GameLimits(hints, chances)
        }
    }
}
