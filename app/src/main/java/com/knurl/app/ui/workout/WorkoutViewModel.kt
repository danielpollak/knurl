package com.knurl.app.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.knurl.app.KnurlApplication
import com.knurl.app.data.repository.TodaysWorkout
import com.knurl.app.data.repository.WorkoutRepository
import com.knurl.app.data.model.WeightUnit
import com.knurl.app.domain.WeightMath
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private var currentWorkout: TodaysWorkout? = null
    private var restTimerJob: Job? = null

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, justFinished = false)
            val workout = repository.loadTodaysWorkout()
            currentWorkout = workout
            _uiState.value = if (workout == null) {
                WorkoutUiState(isLoading = false, hasActiveProgram = false)
            } else {
                WorkoutUiState(
                    isLoading = false,
                    hasActiveProgram = true,
                    programName = workout.programName,
                    dayName = workout.dayName,
                    unitLabel = workout.unit.name.lowercase(),
                    barWeight = workout.barWeight,
                    exercises = workout.exercises.map { exercise ->
                        ExerciseUiState(
                            exerciseId = exercise.exerciseId,
                            name = exercise.name,
                            targetReps = exercise.targetReps,
                            weight = exercise.startingWeight,
                            plateBreakdown = exercise.plateBreakdown,
                            repsPerSet = List<Int?>(exercise.sets) { null }
                        )
                    }
                )
            }
        }
    }

    /** Cycles a set: untouched -> target reps -> down to 0 -> untouched. */
    fun tapSet(exerciseId: Long, setIndex: Int) {
        val state = _uiState.value
        val updatedExercises = state.exercises.map { exercise ->
            if (exercise.exerciseId != exerciseId) {
                exercise
            } else {
                val updatedReps = exercise.repsPerSet.toMutableList()
                val current = updatedReps[setIndex]
                updatedReps[setIndex] = when {
                    current == null -> exercise.targetReps
                    current <= 0 -> null
                    else -> current - 1
                }
                exercise.copy(repsPerSet = updatedReps)
            }
        }
        _uiState.value = state.copy(exercises = updatedExercises)
        startRestTimer()
    }

    /** Adjusts today's weight for one exercise by [direction] steps (+1/-1), never below the bar. */
    fun adjustWeight(exerciseId: Long, direction: Int) {
        val workout = currentWorkout ?: return
        val step = if (workout.unit == WeightUnit.KG) 2.5 else 5.0
        val state = _uiState.value
        _uiState.value = state.copy(
            exercises = state.exercises.map { exercise ->
                if (exercise.exerciseId != exerciseId) {
                    exercise
                } else {
                    val newWeight = (exercise.weight + direction * step).coerceAtLeast(workout.barWeight)
                    exercise.copy(
                        weight = newWeight,
                        plateBreakdown = WeightMath.perSidePlates(newWeight, workout.barWeight, workout.availablePlates)
                    )
                }
            }
        )
    }

    fun skipRest() {
        restTimerJob?.cancel()
        _uiState.value = _uiState.value.copy(restSecondsRemaining = null)
    }

    fun finishWorkout() {
        val workout = currentWorkout ?: return
        restTimerJob?.cancel()
        val repsByExercise = _uiState.value.exercises.associate { it.exerciseId to it.repsPerSet.map { reps -> reps ?: 0 } }
        val weightsByExercise = _uiState.value.exercises.associate { it.exerciseId to it.weight }
        viewModelScope.launch {
            repository.finishWorkout(workout, repsByExercise, weightsByExercise)
            load()
            _uiState.value = _uiState.value.copy(justFinished = true)
        }
    }

    private fun startRestTimer() {
        restTimerJob?.cancel()
        val totalSeconds = currentWorkout?.restTimerSeconds ?: return
        restTimerJob = viewModelScope.launch {
            for (remaining in totalSeconds downTo 0) {
                _uiState.value = _uiState.value.copy(restSecondsRemaining = remaining)
                delay(1_000)
            }
            _uiState.value = _uiState.value.copy(restSecondsRemaining = null)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KnurlApplication
                WorkoutViewModel(WorkoutRepository(app.database))
            }
        }
    }
}
