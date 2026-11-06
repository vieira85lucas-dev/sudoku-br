package com.kaajjo.libresudoku.ui.statistics

import android.text.format.DateUtils
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaajjo.libresudoku.R
import com.kaajjo.libresudoku.core.qqwing.GameDifficulty
import com.kaajjo.libresudoku.core.qqwing.GameType
import com.kaajjo.libresudoku.data.datastore.AppSettingsManager
import com.kaajjo.libresudoku.destinations.GamesHistoryScreenDestination
import com.kaajjo.libresudoku.destinations.SavedGameScreenDestination
import com.kaajjo.libresudoku.ui.components.AnimatedNavigation
import com.kaajjo.libresudoku.ui.components.EmptyScreen
import com.kaajjo.libresudoku.ui.progress.LevelCard
import com.kaajjo.libresudoku.ui.progress.ProgressViewModel
import com.kaajjo.libresudoku.ui.theme.NeoCard
import com.kaajjo.libresudoku.ui.theme.NeoChip
import com.kaajjo.libresudoku.ui.theme.NeoColors
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Destination(style = AnimatedNavigation::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    progressViewModel: ProgressViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val dateFormat by viewModel.dateFormat.collectAsStateWithLifecycle(initialValue = "")
    val records = viewModel.recordList.collectAsState(initial = emptyList()).value
    val savedGames = viewModel.savedGamesList.collectAsState(initial = emptyList()).value
    val context = LocalContext.current
    val level = progressViewModel.level.collectAsState().value

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.statusBars
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.stats_title_a))
                            withStyle(SpanStyle(color = NeoColors.LavenderDeep)) {
                                append(stringResource(R.string.stats_title_b))
                            }
                        },
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = stringResource(R.string.stats_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                NeoCard(
                    shape = CircleShape,
                    shadow = 2.dp,
                    onClick = { navigator.navigate(GamesHistoryScreenDestination()) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_round_history_24),
                        contentDescription = stringResource(R.string.stats_history),
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            ChipRowDifficulty(
                items = listOf(
                    GameDifficulty.Unspecified,
                    GameDifficulty.Easy,
                    GameDifficulty.Moderate,
                    GameDifficulty.Hard,
                    GameDifficulty.Challenge,
                    GameDifficulty.Custom
                ),
                selected = viewModel.selectedDifficulty,
                onSelected = { viewModel.setDifficulty(it) }
            )
            Spacer(Modifier.height(8.dp))
            ChipRowType(
                types = listOf(
                    Pair(GameType.Default9x9, stringResource(R.string.type_default_9x9)),
                    Pair(GameType.Default6x6, stringResource(R.string.type_default_6x6)),
                    Pair(GameType.Default12x12, stringResource(R.string.type_default_12x12)),
                    Pair(GameType.Killer9x9, stringResource(R.string.type_killer_9x9)),
                    Pair(GameType.Killer6x6, stringResource(R.string.type_killer_6x6)),
                    Pair(GameType.Killer12x12, stringResource(R.string.type_killer_12x12))
                ),
                selected = viewModel.selectedType,
                onSelected = { viewModel.setType(it) }
            )
            Spacer(Modifier.height(20.dp))

            if (records.isEmpty()) {
                EmptyScreen(stringResource(R.string.statistics_no_records))
                return@Column
            }

            val noValue = stringResource(R.string.no_value_default)
            val bestTime = DateUtils.formatElapsedTime(records.first().time.seconds)
            val averageTime =
                DateUtils.formatElapsedTime(records.sumOf { it.time.seconds } / records.count())
            val allFilters = viewModel.selectedDifficulty == GameDifficulty.Unspecified
            val started = savedGames.count()
            val completed = savedGames.count { it.completed && !it.giveUp && !it.canContinue }
            val winRate =
                if (savedGames.isNotEmpty())
                    context.getString(R.string.win_rate_percentage, viewModel.getWinRate(savedGames).roundToInt())
                else noValue

            StatsGrid(
                cards = listOf(
                    StatCard(stringResource(R.string.stats_games), started.toString(), NeoColors.Mint, R.drawable.ic_rounded_stadia_controller_24),
                    StatCard(stringResource(R.string.stats_wins), winRate, NeoColors.Lavender, R.drawable.ic_outline_verified_24),
                    StatCard(stringResource(R.string.vp_total), level.totalPoints.toString(), NeoColors.Yellow, R.drawable.ic_outline_star_24),
                    StatCard(stringResource(R.string.best_time), bestTime, NeoColors.Coral, R.drawable.ic_round_hourglass_empty_24)
                )
            )
            Spacer(Modifier.height(12.dp))
            NeoCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), shadow = 2.dp) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatRow(stringResource(R.string.average_time), averageTime)
                    if (allFilters) {
                        StatRow(stringResource(R.string.games_completed), completed.toString())
                        StatRow(stringResource(R.string.current_streak), viewModel.getCurrentStreak(savedGames).toString())
                        StatRow(stringResource(R.string.best_streak), viewModel.getMaxStreak(savedGames).toString())
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            SectionTitle(stringResource(R.string.level_progress_title))
            LevelCard(level)

            if (allFilters) {
                Spacer(Modifier.height(24.dp))
                SectionTitle(stringResource(R.string.stats_by_level))
                LevelBars(
                    bars = listOf(
                        LevelBar(stringResource(R.string.difficulty_easy), records.count { it.difficulty == GameDifficulty.Easy }, NeoColors.Mint),
                        LevelBar(stringResource(R.string.difficulty_moderate), records.count { it.difficulty == GameDifficulty.Moderate }, NeoColors.Yellow),
                        LevelBar(stringResource(R.string.difficulty_hard), records.count { it.difficulty == GameDifficulty.Hard }, NeoColors.Coral),
                        LevelBar(stringResource(R.string.difficulty_challenge), records.count { it.difficulty == GameDifficulty.Challenge }, NeoColors.Lavender)
                    )
                )
            }

            Spacer(Modifier.height(24.dp))
            SectionTitle(
                stringResource(R.string.number_best_games, 5) +
                        if (viewModel.selectedType != GameType.Unspecified && viewModel.selectedDifficulty != GameDifficulty.Unspecified) {
                            " ${stringResource(viewModel.selectedType.resName).lowercase()} " +
                                    stringResource(viewModel.selectedDifficulty.resName).lowercase()
                        } else ""
            )
            var selectedIndex by remember { mutableIntStateOf(0) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                records.take(5).forEachIndexed { index, record ->
                    RecordItem(
                        time = record.time,
                        difficulty = stringResource(record.difficulty.resName),
                        date = record.date.toLocalDateTime(),
                        type = stringResource(record.type.resName),
                        dateFormat = dateFormat,
                        dot = difficultyColor(record.difficulty),
                        onClick = { navigator.navigate(SavedGameScreenDestination(gameUid = record.board_uid)) },
                        onLongClick = {
                            selectedIndex = index
                            viewModel.showDeleteDialog = true
                        }
                    )
                }
            }
            if (viewModel.showDeleteDialog) {
                ShowDeleteDialog(
                    onDismissRequest = { viewModel.showDeleteDialog = false },
                    onConfirm = { viewModel.deleteRecord(records[selectedIndex]) },
                    index = selectedIndex
                )
            }
        }
    }
}

@Composable
fun ShowDeleteDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    index: Int
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismissRequest()
            }) {
                Text(stringResource(R.string.dialog_yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dialog_no))
            }
        },
        title = {
            Text(stringResource(R.string.delete_question))
        },
        text = {
            Text(
                text = stringResource(R.string.delete_record_dialog, index + 1)
            )
        }
    )
}

internal data class StatCard(val label: String, val value: String, val color: Color, val icon: Int)

internal data class LevelBar(val label: String, val count: Int, val color: Color)

internal fun difficultyColor(difficulty: GameDifficulty): Color = when (difficulty) {
    GameDifficulty.Simple, GameDifficulty.Easy -> NeoColors.Mint
    GameDifficulty.Moderate -> NeoColors.Yellow
    GameDifficulty.Hard -> NeoColors.Coral
    else -> NeoColors.Lavender
}

@Composable
internal fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

/** Cartoes 2 a 2: icone colorido, rotulo pequeno e valor grande. */
@Composable
internal fun StatsGrid(cards: List<StatCard>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        cards.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { card ->
                    NeoCard(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        shadow = 3.dp
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            NeoCard(color = card.color, shape = CircleShape, shadow = 0.dp) {
                                Icon(
                                    painter = painterResource(card.icon),
                                    contentDescription = null,
                                    tint = NeoColors.Charcoal,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(22.dp)
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = card.label.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = card.value,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

/** Colunas coloridas com borda: quantas vitorias em cada nivel. */
@Composable
internal fun LevelBars(bars: List<LevelBar>) {
    val max = (bars.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        shadow = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            bars.forEach { bar ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(bar.count.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    // A area das colunas ocupa o espaco que sobra; assim o nome nunca e cortado.
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        val fraction = (bar.count.toFloat() / max).coerceIn(0.06f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(fraction)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bar.color)
                                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = bar.label.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

@Composable
fun StatRow(
    startText: String,
    endText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = startText,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
        )
        Text(
            text = endText,
            fontWeight = FontWeight(700)
        )
    }
}

@Composable
fun ChipRowType(
    modifier: Modifier = Modifier,
    types: List<Pair<GameType, String>>,
    selected: GameType,
    onSelected: (GameType) -> Unit
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(end = 6.dp, bottom = 4.dp)
    ) {
        items(types) { type ->
            NeoChip(
                text = type.second,
                selected = type.first == selected,
                onClick = { onSelected(type.first) }
            )
        }
    }
}

@Composable
fun ChipRowDifficulty(
    items: List<GameDifficulty>,
    selected: GameDifficulty,
    onSelected: (GameDifficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(end = 6.dp, bottom = 4.dp)
    ) {
        items(items) { item ->
            NeoChip(
                text = if (item != GameDifficulty.Unspecified) stringResource(item.resName)
                else stringResource(R.string.statistics_difficulty_filter_all),
                selected = selected == item,
                onClick = { onSelected(item) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordItem(
    modifier: Modifier = Modifier,
    time: Duration,
    date: LocalDateTime,
    difficulty: String,
    type: String,
    dateFormat: String,
    dot: Color = NeoColors.Mint,
    onClick: () -> Unit = { },
    onLongClick: () -> Unit = { }
) {
    NeoCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        shadow = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(dot)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("$difficulty $type", fontWeight = FontWeight.Bold)
                Text(
                    text = date.format(AppSettingsManager.dateFormat(dateFormat)) + "  " +
                            date.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = DateUtils.formatElapsedTime(time.seconds),
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
