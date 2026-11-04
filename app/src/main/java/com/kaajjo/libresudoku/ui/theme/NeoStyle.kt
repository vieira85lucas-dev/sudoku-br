package com.kaajjo.libresudoku.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Paleta fixa do Sudoku BR (cartoes com borda e sombra dura). */
object NeoColors {
    val Background = Color(0xFFF8F7F4)
    val Charcoal = Color(0xFF252525)
    val Lavender = Color(0xFFA78BFA)
    val LavenderDeep = Color(0xFF7C5CE0)
    val Mint = Color(0xFFB8E8D0)
    val Yellow = Color(0xFFFFE49A)
    val Coral = Color(0xFFFF9F8E)
}

/** Sombra dura deslocada, sem desfoque, desenhada atras do conteudo. */
fun Modifier.neoShadow(
    shape: Shape,
    color: Color,
    offset: Dp = 4.dp
): Modifier = drawBehind {
    val o = offset.toPx()
    val outline = shape.createOutline(size, layoutDirection, this)
    translate(o, o) {
        drawOutline(outline = outline, color = color)
    }
}

/** Cartao com borda de 2dp, sombra dura e cantos arredondados. */
@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RoundedCornerShape(24.dp),
    shadow: Dp = 4.dp,
    borderWidth: Dp = 2.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val outline = MaterialTheme.colorScheme.outline
    val shadowModifier = if (shadow > 0.dp) Modifier.neoShadow(shape, outline, shadow) else Modifier
    Box(
        modifier = modifier
            .then(shadowModifier)
            .clip(shape)
            .background(color, shape)
            .border(BorderStroke(borderWidth, outline), shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        content = content
    )
}

/** Botao de acao principal no mesmo estilo dos cartoes. */
@Composable
fun NeoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = NeoColors.Lavender
) {
    NeoCard(
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(18.dp),
        shadow = 3.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier,
                color = NeoColors.Charcoal,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        }
    }
}

/** Pastilha selecionavel (por exemplo, o tipo de tabuleiro). */
@Composable
fun NeoChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeoCard(
        modifier = modifier,
        color = if (selected) NeoColors.Lavender else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        shadow = if (selected) 2.dp else 0.dp,
        onClick = onClick
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = if (selected) NeoColors.Charcoal else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}
