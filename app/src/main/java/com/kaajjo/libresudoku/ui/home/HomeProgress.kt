package com.kaajjo.libresudoku.ui.home

/**
 * Percentual do tabuleiro preenchido para o cartao "Em andamento".
 *
 * As casas dadas contam; as casas preenchidas pelo jogador so contam se baterem com a
 * solucao. Assim o numero fica igual ao do cabecalho do jogo, que nao conta erros.
 *
 * Se a solucao nao estiver disponivel ou nao combinar com o tabuleiro, conta todas as casas preenchidas.
 */
fun filledPercentCorrect(current: String, initial: String?, solved: String?): Int {
    if (current.isEmpty()) return 0
    val usableSolution = solved != null && solved.length == current.length && solved.none(::isEmpty)
    val usableInitial = initial != null && initial.length == current.length
    if (!usableSolution || !usableInitial) {
        return current.count { !isEmpty(it) } * 100 / current.length
    }
    val correct = current.indices.count { i ->
        val value = current[i]
        !isEmpty(value) && (!isEmpty(initial!![i]) || value.equals(solved!![i], ignoreCase = true))
    }
    return correct * 100 / current.length
}

private fun isEmpty(c: Char) = c == '0' || c == '.'
