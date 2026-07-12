package com.knurl.app.data.template

import com.knurl.app.data.model.ProgramType

data class TemplateExercise(
    val exerciseName: String,
    val sets: Int,
    val targetReps: Int,
    val increment: Double
)

data class TemplateDay(val name: String, val exercises: List<TemplateExercise>)

data class ProgramTemplate(val name: String, val type: ProgramType, val days: List<TemplateDay>)

/**
 * One-tap program templates. Exercise names must match entries in [com.knurl.app.data.seed.SeedData.libraryExercises].
 * Chest/Back/Legs and Push/Pull/Legs use 3-4 working sets in an 8-15 rep range rather than 5x5 --
 * these are starting-point defaults, editable from the Programs screen.
 */
object ProgramTemplates {

    val strongLifts5x5 = ProgramTemplate(
        name = "StrongLifts 5x5",
        type = ProgramType.AB,
        days = listOf(
            TemplateDay(
                name = "Workout A",
                exercises = listOf(
                    TemplateExercise("Squat", sets = 5, targetReps = 5, increment = 5.0),
                    TemplateExercise("Bench Press", sets = 5, targetReps = 5, increment = 5.0),
                    TemplateExercise("Barbell Row", sets = 5, targetReps = 5, increment = 5.0)
                )
            ),
            TemplateDay(
                name = "Workout B",
                exercises = listOf(
                    TemplateExercise("Squat", sets = 5, targetReps = 5, increment = 5.0),
                    TemplateExercise("Overhead Press", sets = 5, targetReps = 5, increment = 5.0),
                    TemplateExercise("Deadlift", sets = 1, targetReps = 5, increment = 10.0)
                )
            )
        )
    )

    val chestBackLegs = ProgramTemplate(
        name = "Chest / Back / Legs",
        type = ProgramType.ROTATION,
        days = listOf(
            TemplateDay(
                name = "Chest",
                exercises = listOf(
                    TemplateExercise("Bench Press", sets = 4, targetReps = 8, increment = 5.0),
                    TemplateExercise("Incline Dumbbell Press", sets = 3, targetReps = 10, increment = 2.5),
                    TemplateExercise("Cable Fly", sets = 3, targetReps = 12, increment = 2.5)
                )
            ),
            TemplateDay(
                name = "Back",
                exercises = listOf(
                    TemplateExercise("Deadlift", sets = 3, targetReps = 5, increment = 10.0),
                    TemplateExercise("Lat Pulldown", sets = 4, targetReps = 8, increment = 2.5),
                    TemplateExercise("Face Pull", sets = 3, targetReps = 15, increment = 2.5)
                )
            ),
            TemplateDay(
                name = "Legs",
                exercises = listOf(
                    TemplateExercise("Squat", sets = 4, targetReps = 8, increment = 5.0),
                    TemplateExercise("Leg Press", sets = 3, targetReps = 10, increment = 5.0),
                    TemplateExercise("Calf Raise", sets = 3, targetReps = 15, increment = 2.5)
                )
            )
        )
    )

    val pushPullLegs = ProgramTemplate(
        name = "Push / Pull / Legs",
        type = ProgramType.ROTATION,
        days = listOf(
            TemplateDay(
                name = "Push",
                exercises = listOf(
                    TemplateExercise("Bench Press", sets = 4, targetReps = 8, increment = 5.0),
                    TemplateExercise("Overhead Press", sets = 3, targetReps = 8, increment = 5.0),
                    TemplateExercise("Tricep Pushdown", sets = 3, targetReps = 12, increment = 2.5)
                )
            ),
            TemplateDay(
                name = "Pull",
                exercises = listOf(
                    TemplateExercise("Barbell Row", sets = 4, targetReps = 8, increment = 5.0),
                    TemplateExercise("Lat Pulldown", sets = 3, targetReps = 10, increment = 2.5),
                    TemplateExercise("Bicep Curl", sets = 3, targetReps = 12, increment = 2.5)
                )
            ),
            TemplateDay(
                name = "Legs",
                exercises = listOf(
                    TemplateExercise("Squat", sets = 4, targetReps = 8, increment = 5.0),
                    TemplateExercise("Leg Press", sets = 3, targetReps = 10, increment = 5.0),
                    TemplateExercise("Walking Lunge", sets = 3, targetReps = 12, increment = 2.5)
                )
            )
        )
    )

    val all: List<ProgramTemplate> = listOf(strongLifts5x5, chestBackLegs, pushPullLegs)
}
