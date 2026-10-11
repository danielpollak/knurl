package com.knurl.app.data.seed

import com.knurl.app.data.entity.Exercise
import com.knurl.app.data.entity.Program
import com.knurl.app.data.entity.ProgramDay
import com.knurl.app.data.entity.ProgramExercise
import com.knurl.app.data.entity.Settings
import com.knurl.app.data.entity.WorkingWeight
import com.knurl.app.data.model.MuscleGroup
import com.knurl.app.data.model.ProgramType
import com.knurl.app.data.model.WeightUnit

/**
 * Default data seeded into a fresh database. Exercise names are matched by string
 * against [libraryExercises] when a program template references them, since Room
 * assigns ids only after insertion.
 */
object SeedData {

    const val STARTING_MAIN_LIFT_WEIGHT_LB = 135.0

    /** Default exercise library covering the 5x5 program plus the CBL/PPL templates added in M4. */
    val libraryExercises: List<Exercise> = listOf(
        Exercise(name = "Squat", muscleGroup = MuscleGroup.LEGS, isCustom = false),
        Exercise(name = "Bench Press", muscleGroup = MuscleGroup.CHEST, isCustom = false),
        Exercise(name = "Barbell Row", muscleGroup = MuscleGroup.BACK, isCustom = false),
        Exercise(name = "Overhead Press", muscleGroup = MuscleGroup.SHOULDERS, isCustom = false),
        Exercise(name = "Deadlift", muscleGroup = MuscleGroup.BACK, isCustom = false),
        Exercise(name = "Incline Dumbbell Press", muscleGroup = MuscleGroup.CHEST, isCustom = false),
        Exercise(name = "Cable Fly", muscleGroup = MuscleGroup.CHEST, isCustom = false),
        Exercise(name = "Lat Pulldown", muscleGroup = MuscleGroup.BACK, isCustom = false),
        Exercise(name = "Face Pull", muscleGroup = MuscleGroup.SHOULDERS, isCustom = false),
        Exercise(name = "Leg Press", muscleGroup = MuscleGroup.LEGS, isCustom = false),
        Exercise(name = "Walking Lunge", muscleGroup = MuscleGroup.LEGS, isCustom = false),
        Exercise(name = "Calf Raise", muscleGroup = MuscleGroup.LEGS, isCustom = false),
        Exercise(name = "Bicep Curl", muscleGroup = MuscleGroup.ARMS, isCustom = false),
        Exercise(name = "Tricep Pushdown", muscleGroup = MuscleGroup.ARMS, isCustom = false),
        Exercise(name = "Plank", muscleGroup = MuscleGroup.CORE, isCustom = false)
    )

    val mainLiftNames: Set<String> = setOf(
        "Squat", "Bench Press", "Barbell Row", "Overhead Press", "Deadlift"
    )

    fun defaultSettings(): Settings = Settings(
        unit = WeightUnit.LB,
        barWeight = 45.0,
        restTimerSeconds = 90,
        availablePlates = listOf(45.0, 35.0, 25.0, 10.0, 5.0, 2.5)
    )

    fun fiveByFiveProgram(): Program = Program(
        name = "5x5",
        type = ProgramType.AB,
        isActive = true
    )

    fun fiveByFiveDays(programId: Long): List<ProgramDay> = listOf(
        ProgramDay(programId = programId, name = "Workout A", orderIndex = 0),
        ProgramDay(programId = programId, name = "Workout B", orderIndex = 1)
    )

    /** [exerciseIdByName] must contain every name referenced below. */
    fun fiveByFiveExercises(
        workoutADayId: Long,
        workoutBDayId: Long,
        exerciseIdByName: Map<String, Long>
    ): List<ProgramExercise> {
        fun id(name: String) = requireNotNull(exerciseIdByName[name]) { "Missing exercise: $name" }

        return listOf(
            // Workout A: Squat, Bench Press, Barbell Row — all 5x5.
            ProgramExercise(
                programDayId = workoutADayId,
                exerciseId = id("Squat"),
                sets = 5,
                targetReps = 5,
                increment = 5.0,
                orderIndex = 0
            ),
            ProgramExercise(
                programDayId = workoutADayId,
                exerciseId = id("Bench Press"),
                sets = 5,
                targetReps = 5,
                increment = 5.0,
                orderIndex = 1
            ),
            ProgramExercise(
                programDayId = workoutADayId,
                exerciseId = id("Barbell Row"),
                sets = 5,
                targetReps = 5,
                increment = 5.0,
                orderIndex = 2
            ),
            // Workout B: Squat, Overhead Press 5x5, Deadlift 1x5.
            ProgramExercise(
                programDayId = workoutBDayId,
                exerciseId = id("Squat"),
                sets = 5,
                targetReps = 5,
                increment = 5.0,
                orderIndex = 0
            ),
            ProgramExercise(
                programDayId = workoutBDayId,
                exerciseId = id("Overhead Press"),
                sets = 5,
                targetReps = 5,
                increment = 5.0,
                orderIndex = 1
            ),
            ProgramExercise(
                programDayId = workoutBDayId,
                exerciseId = id("Deadlift"),
                sets = 1,
                targetReps = 5,
                increment = 10.0,
                orderIndex = 2
            )
        )
    }

    fun fiveByFiveWorkingWeights(
        programId: Long,
        exerciseIdByName: Map<String, Long>
    ): List<WorkingWeight> = mainLiftNames.map { name ->
        WorkingWeight(
            programId = programId,
            exerciseId = requireNotNull(exerciseIdByName[name]) { "Missing exercise: $name" },
            weight = STARTING_MAIN_LIFT_WEIGHT_LB,
            failStreak = 0
        )
    }
}
