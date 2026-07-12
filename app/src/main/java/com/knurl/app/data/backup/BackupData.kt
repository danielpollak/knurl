package com.knurl.app.data.backup

import com.knurl.app.data.entity.Exercise
import com.knurl.app.data.entity.Program
import com.knurl.app.data.entity.ProgramDay
import com.knurl.app.data.entity.ProgramExercise
import com.knurl.app.data.entity.Settings
import com.knurl.app.data.entity.SetLog
import com.knurl.app.data.entity.WorkingWeight
import com.knurl.app.data.entity.WorkoutSession
import kotlinx.serialization.Serializable

/** A full, lossless snapshot of every table -- ids are preserved so restore can reinsert rows as-is. */
@Serializable
data class BackupData(
    val exercises: List<Exercise>,
    val programs: List<Program>,
    val programDays: List<ProgramDay>,
    val programExercises: List<ProgramExercise>,
    val workingWeights: List<WorkingWeight>,
    val workoutSessions: List<WorkoutSession>,
    val setLogs: List<SetLog>,
    val settings: Settings?
)
