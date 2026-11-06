package com.kaajjo.libresudoku.core.progress

import com.kaajjo.libresudoku.core.qqwing.GameDifficulty
import com.kaajjo.libresudoku.data.database.model.Record
import com.kaajjo.libresudoku.data.database.model.SavedGame
import kotlin.math.roundToInt

/**
 * Pontuacao "VP" (Vitepi Pontos). Tudo e calculado em cima das partidas ja gravadas,
 * sem campos novos no banco de dados.
 */
object Points {
    /** Pontos de uma vitoria sem erros, conforme o nivel. */
    fun base(difficulty: GameDifficulty): Int = when (difficulty) {
        GameDifficulty.Simple -> 25
        GameDifficulty.Easy -> 50
        GameDifficulty.Moderate -> 100
        GameDifficulty.Hard -> 200
        GameDifficulty.Challenge -> 400
        GameDifficulty.Custom -> 50
        GameDifficulty.Unspecified -> 0
    }

    /** Cada erro desconta 10% dos pontos da partida, no maximo metade. */
    fun forWin(difficulty: GameDifficulty, mistakes: Int): Int {
        val factor = (1.0 - 0.1 * mistakes.coerceAtLeast(0)).coerceAtLeast(0.5)
        return (base(difficulty) * factor).roundToInt()
    }

    /** Soma dos pontos de todas as vitorias registradas. */
    fun total(records: List<Record>, savedGames: List<SavedGame>): Int {
        val mistakesByGame = savedGames.associate { it.uid to it.mistakes }
        return records.sumOf { forWin(it.difficulty, mistakesByGame[it.board_uid] ?: 0) }
    }
}

/** Nivel do jogador a partir do total de VP. */
data class LevelInfo(
    val number: Int,
    val totalPoints: Int,
    /** Pontos ganhos dentro do nivel atual. */
    val intoLevel: Int,
    /** Pontos que o nivel atual cobre, do inicio ao proximo nivel. */
    val levelSpan: Int
) {
    val pointsToNext: Int get() = (levelSpan - intoLevel).coerceAtLeast(0)
    val progress: Float get() = if (levelSpan <= 0) 0f else (intoLevel.toFloat() / levelSpan).coerceIn(0f, 1f)
}

object Levels {
    /** Total de VP para chegar ao nivel [level] (nivel 1 = 0). */
    fun thresholdFor(level: Int): Int {
        val n = (level - 1).coerceAtLeast(0)
        return 150 * n * n
    }

    fun of(totalPoints: Int): LevelInfo {
        var level = 1
        while (totalPoints >= thresholdFor(level + 1)) level++
        val start = thresholdFor(level)
        val next = thresholdFor(level + 1)
        return LevelInfo(level, totalPoints, totalPoints - start, next - start)
    }
}
