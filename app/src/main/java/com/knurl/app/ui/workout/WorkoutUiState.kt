package com.knurl.app.ui.workout

data class WorkoutUiState(
    val isLoading: Boolean = true,
    val hasActiveProgram: Boolean = true,
    val programName: String = "",
    val dayName: String = "",
    val unitLabel: String = "lb",
    val exercises: List<ExerciseUiState> = emptyList(),
    val restSecondsRemaining: Int? = null,
    val justFinished: Boolean = false
)

data class ExerciseUiState(
    val exerciseId: Long,
    val name: String,
    val targetReps: Int,
    val weight: Double,
    val plateBreakdown: List<Double>,
    /** Reps recorded per set; null means the set has not been tapped yet. */
    val repsPerSet: List<Int?>
)
