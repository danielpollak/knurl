package com.knurl.app.ui.programs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.knurl.app.KnurlApplication
import com.knurl.app.data.AppDatabase
import com.knurl.app.data.entity.Exercise
import com.knurl.app.data.model.ProgramType
import com.knurl.app.data.repository.EditableDay
import com.knurl.app.data.repository.EditableExercise
import com.knurl.app.data.repository.ProgramRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EditableExerciseUi(
    val exerciseId: Long,
    val exerciseName: String,
    val sets: Int,
    val targetReps: Int,
    val increment: Double
)

data class EditableDayUi(val name: String, val exercises: List<EditableExerciseUi> = emptyList())

data class ProgramEditorUiState(
    val programName: String = "",
    val days: List<EditableDayUi> = listOf(EditableDayUi(name = "Day 1")),
    val saved: Boolean = false
)

class ProgramEditorViewModel(
    database: AppDatabase,
    private val programRepository: ProgramRepository
) : ViewModel() {

    val libraryExercises: StateFlow<List<Exercise>> = database.exerciseDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(ProgramEditorUiState())
    val uiState: StateFlow<ProgramEditorUiState> = _uiState.asStateFlow()

    fun updateProgramName(name: String) {
        _uiState.value = _uiState.value.copy(programName = name)
    }

    fun addDay() {
        val state = _uiState.value
        _uiState.value = state.copy(days = state.days + EditableDayUi(name = "Day ${state.days.size + 1}"))
    }

    fun removeDay(dayIndex: Int) {
        val state = _uiState.value
        if (state.days.size <= 1) return
        _uiState.value = state.copy(days = state.days.filterIndexed { index, _ -> index != dayIndex })
    }

    fun updateDayName(dayIndex: Int, name: String) {
        updateDay(dayIndex) { it.copy(name = name) }
    }

    fun addExercise(dayIndex: Int, exercise: Exercise, sets: Int, targetReps: Int, increment: Double) {
        updateDay(dayIndex) { day ->
            day.copy(
                exercises = day.exercises + EditableExerciseUi(
                    exerciseId = exercise.id,
                    exerciseName = exercise.name,
                    sets = sets,
                    targetReps = targetReps,
                    increment = increment
                )
            )
        }
    }

    fun removeExercise(dayIndex: Int, exerciseIndex: Int) {
        updateDay(dayIndex) { day ->
            day.copy(exercises = day.exercises.filterIndexed { index, _ -> index != exerciseIndex })
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.programName.isBlank() || state.days.any { it.exercises.isEmpty() }) return

        val type = if (state.days.size == 2) ProgramType.AB else ProgramType.ROTATION
        val days = state.days.map { day ->
            EditableDay(
                name = day.name,
                exercises = day.exercises.map { exercise ->
                    EditableExercise(exercise.exerciseId, exercise.sets, exercise.targetReps, exercise.increment)
                }
            )
        }
        viewModelScope.launch {
            programRepository.createProgram(state.programName, type, days, activate = true)
            _uiState.value = _uiState.value.copy(saved = true)
        }
    }

    private inline fun updateDay(dayIndex: Int, transform: (EditableDayUi) -> EditableDayUi) {
        val state = _uiState.value
        val updatedDays = state.days.mapIndexed { index, day -> if (index == dayIndex) transform(day) else day }
        _uiState.value = state.copy(days = updatedDays)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KnurlApplication
                ProgramEditorViewModel(app.database, ProgramRepository(app.database))
            }
        }
    }
}
