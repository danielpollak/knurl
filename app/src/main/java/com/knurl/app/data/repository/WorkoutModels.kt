package com.knurl.app.data.repository

import com.knurl.app.data.model.MuscleGroup
import com.knurl.app.data.model.WeightUnit

data class WorkoutExerciseUi(
    val programExerciseId: Long,
    val exerciseId: Long,
    val name: String,
    val muscleGroup: MuscleGroup,
    val sets: Int,
    val targetReps: Int,
    val increment: Double,
    val startingWeight: Double,
    val startingFailStreak: Int,
    val plateBreakdown: List<Double>
)

data class TodaysWorkout(
    val programId: Long,
    val programName: String,
    val programDayId: Long,
    val dayName: String,
    val epochDate: Long,
    val unit: WeightUnit,
    val barWeight: Double,
    val availablePlates: List<Double>,
    val restTimerSeconds: Int,
    val exercises: List<WorkoutExerciseUi>
)
