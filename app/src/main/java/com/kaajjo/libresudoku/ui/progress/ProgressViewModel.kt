package com.kaajjo.libresudoku.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaajjo.libresudoku.core.progress.Levels
import com.kaajjo.libresudoku.core.progress.LevelInfo
import com.kaajjo.libresudoku.core.progress.Points
import com.kaajjo.libresudoku.domain.repository.RecordRepository
import com.kaajjo.libresudoku.domain.repository.SavedGameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Entrega o nivel e o total de VP do jogador para as telas que mostram o progresso. */
@HiltViewModel
class ProgressViewModel @Inject constructor(
    recordRepository: RecordRepository,
    savedGameRepository: SavedGameRepository
) : ViewModel() {
    val level: StateFlow<LevelInfo> = combine(
        recordRepository.getAllSortByTime(),
        savedGameRepository.getAll()
    ) { records, saved -> Levels.of(Points.total(records, saved)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Levels.of(0))
}
