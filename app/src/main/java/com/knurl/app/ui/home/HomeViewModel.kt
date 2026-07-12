package com.knurl.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.knurl.app.KnurlApplication
import com.knurl.app.data.AppDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val exerciseCount: Int = 0,
    val activeProgramName: String = "",
    val mainLiftWeights: List<Pair<String, Double>> = emptyList()
)

class HomeViewModel(database: AppDatabase) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        database.exerciseDao().getAll(),
        database.programDao().getActive().filterNotNull().flatMapLatest { program ->
            database.workingWeightDao().getAllForProgram(program.id)
                .map { weights -> program.name to weights }
        }
    ) { exercises, (programName, weights) ->
        val exerciseNameById = exercises.associateBy({ it.id }, { it.name })
        HomeUiState(
            exerciseCount = exercises.size,
            activeProgramName = programName,
            mainLiftWeights = weights
                .map { ww -> (exerciseNameById[ww.exerciseId] ?: "?") to ww.weight }
                .sortedBy { it.first }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KnurlApplication
                HomeViewModel(app.database)
            }
        }
    }
}
