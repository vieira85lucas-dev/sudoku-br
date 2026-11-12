package com.kaajjo.libresudoku.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.kaajjo.libresudoku.ui.theme.ColorUtils.blend
import com.kaajjo.libresudoku.ui.theme.ColorUtils.harmonizeWithPrimary

object BoardColors {
    /**
     * Numeros colocados pelo jogador: roxo vivo no claro e amarelo no escuro,
     * sempre bem diferentes dos numeros iniciais.
     */
    inline val foregroundColor: Color
        @Composable
        get() = if (isDark) NeoColors.Yellow else MaterialTheme.colorScheme.secondary

    inline val notesColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.onSurfaceVariant

    /** Numeros iniciais do jogo: grafite (ou branco no escuro), em negrito. */
    inline val altForegroundColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.onSurface

    inline val errorColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.error

    /** Destaques (celula, linha, coluna e numeros iguais): lavanda no claro, cinza claro no escuro. */
    inline val highlightColor: Color
        @Composable
        get() = if (isDark) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary

    inline val thickLineColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.outline

    inline val thinLineColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.outline.copy(if (isDark) 0.5f else 0.35f)

    /** Verdadeiro quando o fundo do app e escuro. */
    inline val isDark: Boolean
        @Composable
        get() = MaterialTheme.colorScheme.background.luminance() < 0.5f
}

interface SudokuBoardColors {
    val foregroundColor: Color
    val notesColor: Color
    val altForegroundColor: Color
    val errorColor: Color
    val highlightColor: Color
    val thickLineColor: Color
    val thinLineColor: Color
}

class SudokuBoardColorsImpl(
    override val foregroundColor: Color = Color.White,
    override val notesColor: Color = Color.White,
    override val altForegroundColor: Color = Color.White,
    override val errorColor: Color = Color.White,
    override val highlightColor: Color = Color.White,
    override val thickLineColor: Color = Color.White,
    override val thinLineColor: Color = Color.White,
) : SudokuBoardColors
