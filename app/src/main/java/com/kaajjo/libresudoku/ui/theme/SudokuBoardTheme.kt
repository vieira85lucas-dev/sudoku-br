package com.kaajjo.libresudoku.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kaajjo.libresudoku.ui.theme.ColorUtils.blend
import com.kaajjo.libresudoku.ui.theme.ColorUtils.harmonizeWithPrimary

object BoardColors {
    inline val foregroundColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.onSurface

    inline val notesColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.onSurfaceVariant

    inline val altForegroundColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.secondary

    inline val errorColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.error

    inline val highlightColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.primary

    inline val thickLineColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.outline

    inline val thinLineColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.outline.copy(0.18f)
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
