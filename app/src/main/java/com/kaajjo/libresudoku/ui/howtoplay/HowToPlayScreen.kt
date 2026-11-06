package com.kaajjo.libresudoku.ui.howtoplay

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaajjo.libresudoku.R
import com.kaajjo.libresudoku.ui.components.AnimatedNavigation
import com.kaajjo.libresudoku.ui.game.components.KeyboardItem
import com.kaajjo.libresudoku.ui.game.components.ToolbarItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PAGE_COUNT = 6

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Destination(style = AnimatedNavigation::class)
@Composable
fun HowToPlayScreen(navigator: DestinationsNavigator) {
    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })
    val scope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == PAGE_COUNT - 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.how_to_play_title)) },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(
                pagerState = pagerState,
                isLast = isLast,
                onBack = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                onNext = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                onFinish = { navigator.popBackStack() }
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) { page ->
            val active = pagerState.currentPage == page
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (page) {
                    0 -> GoalPage(active)
                    1 -> SelectCellPage(active)
                    2 -> LockDigitPage(active)
                    3 -> NotesPage(active)
                    4 -> ErasePage(active)
                    else -> RulesPage()
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    pagerState: PagerState,
    isLast: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.primary) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                if (pagerState.currentPage > 0) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.how_to_play_previous),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(PAGE_COUNT) { index ->
                    val selected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .size(if (selected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = if (selected) 1f else 0.5f)
                            )
                    )
                }
            }
            if (isLast) {
                TextButton(onClick = onFinish) {
                    Text(
                        text = stringResource(R.string.how_to_play_done),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                IconButton(onClick = onNext) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.how_to_play_next),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

// ------------------------------------------------------------------ animação

/** Passa pelas fases 0 until [count] em loop enquanto a página está visível. */
@Composable
private fun rememberPhase(active: Boolean, count: Int, stepMs: Long = 1300L): Int {
    var phase by remember { mutableIntStateOf(0) }
    LaunchedEffect(active) {
        phase = 0
        if (active) {
            while (true) {
                for (p in 0 until count) {
                    phase = p
                    delay(stepMs)
                }
            }
        }
    }
    return phase
}

/** Anel pulsante usado para mostrar onde tocar nos botões. */
@Composable
private fun Modifier.tapRing(visible: Boolean): Modifier {
    if (!visible) return this
    val transition = rememberInfiniteTransition(label = "ring")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "ringProgress"
    )
    val color = MaterialTheme.colorScheme.primary
    return this.drawWithContent {
        drawContent()
        val radius = size.minDimension * (0.35f + 0.25f * progress)
        drawCircle(
            color = color.copy(alpha = 0.8f * (1f - progress)),
            radius = radius,
            style = Stroke(width = 3.dp.toPx())
        )
        drawCircle(color = color.copy(alpha = 0.5f), radius = size.minDimension * 0.12f)
    }
}

// ------------------------------------------------------------------ componentes

@Composable
private fun StepTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun StepCaption(text: String) {
    Spacer(Modifier.height(16.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun NumberRow(selected: Int = 0, pulse: Int = 0) {
    Row(Modifier.fillMaxWidth()) {
        for (n in 1..9) {
            KeyboardItem(
                modifier = Modifier
                    .weight(1f)
                    .tapRing(pulse == n),
                number = n,
                onClick = { },
                selected = selected == n
            )
        }
    }
}

@Composable
private fun ToolRow(notesOn: Boolean = false, eraseOn: Boolean = false, pulse: Int = -1) {
    // 0 = desfazer, 1 = dica, 2 = anotar, 3 = apagar
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ToolbarItem(
            modifier = Modifier
                .weight(1f)
                .tapRing(pulse == 0),
            painter = painterResource(R.drawable.ic_round_undo_24)
        )
        ToolbarItem(
            modifier = Modifier
                .weight(1f)
                .tapRing(pulse == 1),
            painter = painterResource(R.drawable.ic_lightbulb_stars_24)
        )
        ToolbarItem(
            modifier = Modifier
                .weight(1f)
                .tapRing(pulse == 2),
            painter = painterResource(R.drawable.ic_round_edit_24),
            toggled = notesOn
        )
        ToolbarItem(
            modifier = Modifier
                .weight(1f)
                .tapRing(pulse == 3),
            painter = painterResource(R.drawable.ic_eraser_24),
            toggled = eraseOn
        )
    }
}

// ------------------------------------------------------------------ páginas

@Composable
internal fun GoalPage(active: Boolean) {
    val phase = rememberPhase(active, count = 3, stepMs = 1800L)
    val row = 4
    val col = 4
    val houses = when (phase) {
        0 -> rowCells(row)
        1 -> colCells(col)
        else -> boxCells(row, col)
    }
    val label = when (phase) {
        0 -> stringResource(R.string.how_to_play_row)
        1 -> stringResource(R.string.how_to_play_column)
        else -> stringResource(R.string.how_to_play_block)
    }
    StepTitle(stringResource(R.string.how_to_play_goal_title))
    TutorialBoard(TutorialBoardState(selected = cellIndex(row, col), houses = houses))
    Spacer(Modifier.height(12.dp))
    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
    StepCaption(stringResource(R.string.how_to_play_goal_text))
}

@Composable
internal fun SelectCellPage(active: Boolean) {
    val phase = rememberPhase(active, count = 4)
    val target = cellIndex(0, 2) // solução: 4
    val selected = if (phase >= 1) target else null
    val houses = if (phase >= 1) rowCells(0) + colCells(2) + boxCells(0, 2) else emptySet()
    val values = if (phase >= 2) mapOf(target to 4) else emptyMap()
    StepTitle(stringResource(R.string.how_to_play_select_title))
    TutorialBoard(
        TutorialBoardState(
            userValues = values,
            selected = selected,
            houses = houses,
            pulse = if (phase == 0) target else null
        )
    )
    Spacer(Modifier.height(12.dp))
    NumberRow(pulse = if (phase == 1) 4 else 0)
    StepCaption(stringResource(R.string.how_to_play_select_text))
}

@Composable
internal fun LockDigitPage(active: Boolean) {
    val phase = rememberPhase(active, count = 5)
    val first = cellIndex(1, 1) // solução: 7
    val second = cellIndex(3, 3) // solução: 7
    val values = buildMap {
        if (phase >= 3) put(first, 7)
        if (phase >= 4) put(second, 7)
    }
    val pulse = when (phase) {
        2 -> first
        3 -> second
        else -> null
    }
    StepTitle(stringResource(R.string.how_to_play_lock_title))
    TutorialBoard(
        TutorialBoardState(
            userValues = values,
            digit = if (phase >= 1) 7 else 0,
            pulse = pulse
        )
    )
    Spacer(Modifier.height(12.dp))
    NumberRow(selected = if (phase >= 1) 7 else 0, pulse = if (phase == 0) 7 else 0)
    StepCaption(stringResource(R.string.how_to_play_lock_text))
}

@Composable
internal fun NotesPage(active: Boolean) {
    val phase = rememberPhase(active, count = 6)
    val target = cellIndex(0, 2) // candidatos: 1, 2 e 4
    val notes = when {
        phase >= 5 -> setOf(1, 2, 4)
        phase == 4 -> setOf(1, 2)
        phase == 3 -> setOf(1)
        else -> emptySet()
    }
    val keyPulse = when (phase) {
        2 -> 1
        3 -> 2
        4 -> 4
        else -> 0
    }
    StepTitle(stringResource(R.string.how_to_play_notes_title))
    TutorialBoard(
        TutorialBoardState(
            notes = mapOf(target to notes),
            selected = if (phase >= 1) target else null,
            houses = if (phase >= 1) rowCells(0) + colCells(2) + boxCells(0, 2) else emptySet(),
            pulse = if (phase == 1) target else null
        )
    )
    Spacer(Modifier.height(12.dp))
    NumberRow(pulse = keyPulse)
    Spacer(Modifier.height(8.dp))
    ToolRow(notesOn = phase >= 1, pulse = if (phase == 0) 2 else -1)
    StepCaption(stringResource(R.string.how_to_play_notes_text))
}

@Composable
internal fun ErasePage(active: Boolean) {
    val phase = rememberPhase(active, count = 3, stepMs = 1500L)
    val target = cellIndex(0, 2)
    StepTitle(stringResource(R.string.how_to_play_erase_title))
    TutorialBoard(
        TutorialBoardState(
            notes = if (phase < 2) mapOf(target to setOf(1, 2, 4)) else emptyMap(),
            selected = target,
            houses = rowCells(0) + colCells(2) + boxCells(0, 2),
            pulse = if (phase == 1) target else null
        )
    )
    Spacer(Modifier.height(12.dp))
    ToolRow(eraseOn = phase >= 1, pulse = if (phase == 0) 3 else -1)
    StepCaption(stringResource(R.string.how_to_play_erase_text))
}

@Composable
internal fun RulesPage() {
    StepTitle(stringResource(R.string.how_to_play_rules_title))
    Spacer(Modifier.height(8.dp))
    RuleCard(
        title = stringResource(R.string.how_to_play_rule_mistakes_title),
        text = stringResource(R.string.how_to_play_rule_mistakes_text)
    )
    RuleCard(
        title = stringResource(R.string.how_to_play_rule_second_chance_title),
        text = stringResource(R.string.how_to_play_rule_second_chance_text)
    )
    RuleCard(
        title = stringResource(R.string.how_to_play_rule_hints_title),
        text = stringResource(R.string.how_to_play_rule_hints_text)
    )
    RuleCard(
        title = stringResource(R.string.how_to_play_rule_ads_title),
        text = stringResource(R.string.how_to_play_rule_ads_text)
    )
}

@Composable
private fun RuleCard(title: String, text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
