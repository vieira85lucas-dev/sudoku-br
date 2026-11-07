package com.kaajjo.libresudoku.ui.home

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import com.kaajjo.libresudoku.data.datastore.AppSettingsManager
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaajjo.libresudoku.R
import com.kaajjo.libresudoku.core.qqwing.GameDifficulty
import com.kaajjo.libresudoku.core.qqwing.GameType
import com.kaajjo.libresudoku.core.utils.toFormattedString
import com.kaajjo.libresudoku.data.database.model.SavedGame
import com.kaajjo.libresudoku.destinations.GameScreenDestination
import com.kaajjo.libresudoku.destinations.HowToPlayScreenDestination
import com.kaajjo.libresudoku.ui.components.AnimatedNavigation
import com.kaajjo.libresudoku.ui.components.ScrollbarLazyColumn
import com.kaajjo.libresudoku.ui.components.generation.GenerationDialog
import com.kaajjo.libresudoku.ui.components.generation.GenerationState
import com.kaajjo.libresudoku.ui.components.board.BoardPreview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.time.ZonedDateTime
import kotlin.math.sqrt
import kotlin.time.toKotlinDuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.kaajjo.libresudoku.ui.theme.NeoButton
import com.kaajjo.libresudoku.ui.theme.NeoCard
import com.kaajjo.libresudoku.ui.theme.NeoChip
import com.kaajjo.libresudoku.ui.theme.NeoColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Destination(style = AnimatedNavigation::class)
@RootNavGraph(start = true)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), navigator: DestinationsNavigator) {
    var continueGameDialog by rememberSaveable { mutableStateOf(false) }
    var lastGamesBottomSheet by rememberSaveable { mutableStateOf(false) }

    val userName by viewModel.userName.collectAsStateWithLifecycle()
    var nameDialog by rememberSaveable { mutableStateOf(false) }
    val lastGame by viewModel.lastSavedGame.collectAsStateWithLifecycle()
    val lastGames by viewModel.lastGames.collectAsStateWithLifecycle(initialValue = emptyMap())
    val saveSelectedGameDifficultyType by
            viewModel.saveSelectedGameDifficultyType.collectAsStateWithLifecycle(false)
    val lastSelectedGameDifficultyType by
            viewModel.lastSelectedGameDifficultyType.collectAsStateWithLifecycle(
                    Pair(GameDifficulty.Easy, GameType.Default9x9)
            )

    LaunchedEffect(saveSelectedGameDifficultyType) {
        if (saveSelectedGameDifficultyType) {
            val (difficulty, type) = lastSelectedGameDifficultyType
            viewModel.selectedDifficulty = difficulty
            viewModel.selectedType = type
        }
    }

    val generationState by viewModel.generation.state.collectAsStateWithLifecycle()
    LaunchedEffect(generationState) {
        if (generationState == GenerationState.Completed) {
            viewModel.generation.cancel()
            navigator.navigate(GameScreenDestination(viewModel.insertedBoardUid, playedBefore = false))
        }
    }
    GenerationDialog(
        state = generationState,
        onRetry = viewModel.generation::retry,
        onCancel = viewModel.generation::cancel
    )

    val continuableGame = lastGame?.takeIf { !it.completed }
    val onContinue: () -> Unit = {
        if (lastGames.size <= 1) {
            lastGame?.let {
                navigator.navigate(GameScreenDestination(gameUid = it.uid, playedBefore = true))
            }
        } else {
            lastGamesBottomSheet = true
        }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        val lastBoard = continuableGame?.let { game ->
            lastGames.entries.firstOrNull { it.key.uid == game.uid }?.value
        }
        HomeContent(
            modifier = Modifier.padding(paddingValues),
            continueInfo = continuableGame?.let {
                ContinueInfo(
                    title = lastBoard?.let { board ->
                        "${stringResource(board.difficulty.resName)} ${stringResource(board.type.resName)}"
                    } ?: stringResource(R.string.action_continue),
                    percent = filledPercent(it.currentBoard),
                    time = it.timer.toKotlinDuration().toFormattedString()
                )
            },
            difficulties = viewModel.difficulties,
            selectedDifficulty = viewModel.selectedDifficulty,
            onSelectDifficulty = { viewModel.selectedDifficulty = it },
            types = viewModel.types,
            selectedType = viewModel.selectedType,
            onSelectType = { viewModel.selectedType = it },
            onContinue = onContinue,
            onPlay = {
                if (continuableGame != null) continueGameDialog = true else viewModel.startGame()
            },
            onHelp = { navigator.navigate(HowToPlayScreenDestination()) },
            userName = userName,
            onEditName = { nameDialog = true }
        )

        if (nameDialog) {
            NameDialog(
                current = userName,
                onDismiss = { nameDialog = false },
                onSave = {
                    viewModel.setUserName(it)
                    nameDialog = false
                }
            )
        }

        if (continueGameDialog) {
            AlertDialog(
                    title = { Text(stringResource(R.string.dialog_new_game_title)) },
                    text = { Text(stringResource(R.string.dialog_new_game_text)) },
                    confirmButton = {
                        TextButton(
                                onClick = {
                                    continueGameDialog = false
                                    viewModel.startGame()
                                }
                        ) { Text(stringResource(R.string.dialog_new_game_positive)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { continueGameDialog = false }) {
                            Text(stringResource(R.string.action_cancel))
                        }
                    },
                    onDismissRequest = { continueGameDialog = false }
            )
        }

        if (lastGamesBottomSheet) {
            ModalBottomSheet(onDismissRequest = { lastGamesBottomSheet = false }) {
                Text(
                        text =
                                pluralStringResource(
                                        id = R.plurals.last_x_games,
                                        count = lastGames.size,
                                        lastGames.size
                                ),
                        modifier = Modifier.padding(start = 12.dp),
                        style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                ScrollbarLazyColumn(
                        contentPadding = PaddingValues(horizontal = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.clip(MaterialTheme.shapes.large)
                ) {
                    items(lastGames.toList()) { item ->
                        SavedSudokuPreview(
                                board = item.first.currentBoard,
                                difficulty = stringResource(item.second.difficulty.resName),
                                type = stringResource(item.second.type.resName),
                                savedGame = item.first,
                                onClick = {
                                    navigator.navigate(
                                            GameScreenDestination(
                                                    gameUid = item.first.uid,
                                                    playedBefore = true
                                            )
                                    )
                                    lastGamesBottomSheet = false
                                }
                        )
                    }
                }
            }
        }
    }
}

/** Dados do cartao "Em andamento". */
internal data class ContinueInfo(val title: String, val percent: Int, val time: String)

/** Conteudo da tela inicial, sem depender do ViewModel. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun HomeContent(
    continueInfo: ContinueInfo?,
    difficulties: List<GameDifficulty>,
    selectedDifficulty: GameDifficulty,
    onSelectDifficulty: (GameDifficulty) -> Unit,
    types: List<GameType>,
    selectedType: GameType,
    onSelectType: (GameType) -> Unit,
    onContinue: () -> Unit,
    onPlay: () -> Unit,
    onHelp: () -> Unit,
    userName: String,
    onEditName: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        HomeHeader(userName = userName, onEditName = onEditName, onHelp = onHelp)

        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.home_headline_a))
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                    append(stringResource(R.string.home_headline_b))
                }
            },
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp, lineHeight = 38.sp)
        )

        if (continueInfo != null) {
            ContinueCard(
                title = continueInfo.title,
                percent = continueInfo.percent,
                time = continueInfo.time,
                onClick = onContinue
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = stringResource(R.string.new_game),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
            DifficultyGrid(
                difficulties = difficulties,
                selected = selectedDifficulty,
                onSelect = onSelectDifficulty
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                types.forEach { type ->
                    NeoChip(
                        text = stringResource(type.resName),
                        selected = selectedType == type,
                        onClick = { onSelectType(type) }
                    )
                }
            }
        }

        NeoButton(
            text = stringResource(R.string.action_play),
            onClick = onPlay,
            modifier = Modifier.fillMaxWidth(),
            color = NeoColors.Lavender
        )
    }
}

/**
 * Cabecalho: a esquerda o jogador ("Ola, nome!", toque para mudar o nome);
 * a direita o logo e o nome do app, e o botao "?" do tutorial.
 */
@Composable
private fun HomeHeader(userName: String, onEditName: () -> Unit, onHelp: () -> Unit) {
    val shownName = userName.ifBlank { stringResource(R.string.home_default_name) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        NeoCard(
            modifier = Modifier.size(40.dp),
            color = NeoColors.Lavender,
            shape = RoundedCornerShape(14.dp),
            shadow = 2.dp,
            onClick = onEditName
        ) {
            val initial = userName.trim().firstOrNull()?.uppercaseChar()
            if (initial != null) {
                Text(
                    text = initial.toString(),
                    modifier = Modifier.align(Alignment.Center),
                    color = NeoColors.Charcoal,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = stringResource(R.string.home_edit_name),
                    tint = NeoColors.Charcoal,
                    modifier = Modifier.align(Alignment.Center).size(24.dp)
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onEditName)
        ) {
            Text(
                text = stringResource(R.string.home_hello).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = shownName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(8.dp))
        NeoCard(
            modifier = Modifier.size(32.dp),
            color = NeoColors.Lavender,
            shape = RoundedCornerShape(11.dp),
            shadow = 2.dp
        ) {
            Icon(
                imageVector = Icons.Rounded.GridView,
                contentDescription = null,
                tint = NeoColors.Charcoal,
                modifier = Modifier.align(Alignment.Center).size(18.dp)
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
            maxLines = 1
        )
        Spacer(Modifier.width(8.dp))
        NeoCard(
            modifier = Modifier.size(34.dp),
            color = NeoColors.Yellow,
            shape = androidx.compose.foundation.shape.CircleShape,
            shadow = 2.dp,
            onClick = onHelp
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                contentDescription = stringResource(R.string.how_to_play_help),
                tint = NeoColors.Charcoal,
                modifier = Modifier.align(Alignment.Center).size(21.dp)
            )
        }
    }
}

/** Pergunta como chamar o jogador; o nome aparece no cabecalho da tela inicial. */
@Composable
private fun NameDialog(current: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf(current) }
    AlertDialog(
        title = { Text(stringResource(R.string.home_name_dialog_title)) },
        text = {
            androidx.compose.material3.OutlinedTextField(
                value = text,
                onValueChange = { text = it.take(AppSettingsManager.MAX_USER_NAME_LENGTH) },
                singleLine = true,
                label = { Text(stringResource(R.string.home_name_dialog_hint)) }
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(text) }) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
        onDismissRequest = onDismiss
    )
}

@Composable
private fun ContinueCard(title: String, percent: Int, time: String, onClick: () -> Unit) {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        color = NeoColors.Mint,
        shape = RoundedCornerShape(28.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.home_in_progress).uppercase(),
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(50))
                    .border(1.dp, NeoColors.Charcoal, RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                color = NeoColors.Charcoal,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                color = NeoColors.Charcoal,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = stringResource(R.string.home_filled_time, percent, time),
                color = NeoColors.Charcoal.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.45f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(percent / 100f)
                            .height(12.dp)
                            .background(NeoColors.Charcoal, RoundedCornerShape(50))
                    )
                }
                Spacer(Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(NeoColors.Charcoal, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = stringResource(R.string.action_continue),
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyGrid(
    difficulties: List<GameDifficulty>,
    selected: GameDifficulty,
    onSelect: (GameDifficulty) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        difficulties.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { difficulty ->
                    val style = difficultyStyle(difficulty)
                    DifficultyTile(
                        label = stringResource(difficulty.resName),
                        icon = style.first,
                        accent = style.second,
                        selected = difficulty == selected,
                        onClick = { onSelect(difficulty) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

private fun difficultyStyle(difficulty: GameDifficulty): Pair<ImageVector, Color> = when (difficulty) {
    GameDifficulty.Simple, GameDifficulty.Easy -> Icons.Rounded.Eco to NeoColors.Mint
    GameDifficulty.Moderate -> Icons.Rounded.Bolt to NeoColors.Yellow
    GameDifficulty.Hard -> Icons.Rounded.LocalFireDepartment to NeoColors.Coral
    else -> Icons.Rounded.WorkspacePremium to NeoColors.Lavender
}

@Composable
private fun DifficultyTile(
    label: String,
    icon: ImageVector,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeoCard(
        modifier = modifier,
        color = if (selected) accent else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        shadow = if (selected) 3.dp else 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(accent, CircleShape)
                    .border(2.dp, NeoColors.Charcoal, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeoColors.Charcoal,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                maxLines = 1,
                color = if (selected) NeoColors.Charcoal else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/** Percentual de casas preenchidas (as dadas contam), a partir do tabuleiro salvo. */
private fun filledPercent(board: String): Int {
    if (board.isEmpty()) return 0
    return board.count { it != '0' && it != '.' } * 100 / board.length
}

@Composable
fun GeneratingDialog(onDismiss: () -> Unit, text: String) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Column { Text(text = text, style = MaterialTheme.typography.bodyLarge) }
                    Column(modifier = Modifier.padding(top = 16.dp)) { CircularProgressIndicator() }
                }
            }
        }
    }
}

@Composable
fun HorizontalPicker(
        modifier: Modifier = Modifier,
        text: String,
        onLeftClick: () -> Unit,
        onRightClick: () -> Unit
) {
    Row(
            modifier = modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 36.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onLeftClick) {
            Icon(
                    painter = painterResource(R.drawable.ic_round_keyboard_arrow_left_24),
                    contentDescription = null
            )
        }
        AnimatedContent(
                targetState = text,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "Animated text"
        ) { text -> Text(text) }
        IconButton(onClick = onRightClick) {
            Icon(
                    painter = painterResource(R.drawable.ic_round_keyboard_arrow_right_24),
                    contentDescription = null
            )
        }
    }
}

@Composable
fun SavedSudokuPreview(
        board: String,
        difficulty: String,
        type: String,
        savedGame: SavedGame,
        modifier: Modifier = Modifier,
        onClick: () -> Unit = {}
) {
    Box(
            modifier =
                    modifier.clip(MaterialTheme.shapes.medium)
                            .clickable(onClick = onClick)
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                        modifier =
                                Modifier.clip(RoundedCornerShape(4.dp))
                                        .size(130.dp)
                                        .align(Alignment.CenterVertically)
                ) { BoardPreview(size = sqrt(board.length.toFloat()).toInt(), boardString = board) }
                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text("$difficulty $type")
                    Text(
                            text =
                                    stringResource(
                                            R.string.history_item_time,
                                            savedGame.timer.toKotlinDuration().toFormattedString()
                                    )
                    )
                    if (savedGame.lastPlayed != null) {
                        val lastPlayedRelative by
                                remember(savedGame) {
                                    mutableStateOf(
                                            DateUtils.getRelativeTimeSpanString(
                                                            savedGame.lastPlayed.toEpochSecond() *
                                                                    1000L,
                                                            ZonedDateTime.now().toEpochSecond() *
                                                                    1000L,
                                                            DateUtils.MINUTE_IN_MILLIS
                                                    )
                                                    .toString()
                                    )
                                }
                        Text(lastPlayedRelative)
                    }
                }
            }
            IconButton(onClick = onClick) {
                Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null
                )
            }
        }
    }
}
