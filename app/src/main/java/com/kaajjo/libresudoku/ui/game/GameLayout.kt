package com.kaajjo.libresudoku.ui.game

/**
 * Decide se a tela de partida precisa do modo compacto: quando o conteudo, medido de verdade,
 * e mais alto que o espaco livre (por exemplo, o 12x12, que tem duas linhas de teclado, em
 * celulares menores com banner de anuncio). No modo compacto o cabecalho, o teclado e os
 * botoes ficam mais baixos. Se mesmo assim nao couber, a tela rola.
 */
fun needsCompactLayout(contentHeightPx: Int, availableHeightPx: Int): Boolean =
    availableHeightPx > 0 && contentHeightPx > availableHeightPx
