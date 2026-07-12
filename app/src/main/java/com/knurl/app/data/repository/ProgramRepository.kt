package com.knurl.app.data.repository

import com.knurl.app.data.AppDatabase
import com.knurl.app.data.entity.Program
import com.knurl.app.data.entity.ProgramDay
import com.knurl.app.data.entity.ProgramExercise
import com.knurl.app.data.entity.WorkingWeight
import com.knurl.app.data.model.ProgramType
import com.knurl.app.data.seed.SeedData
import com.knurl.app.data.template.ProgramTemplate
import kotlinx.coroutines.flow.first

/** One exercise entry as authored in the program editor: an existing library exercise plus set/rep/increment. */
data class EditableExercise(
    val exerciseId: Long,
    val sets: Int,
    val targetReps: Int,
    val increment: Double
)

data class EditableDay(val name: String, val exercises: List<EditableExercise>)

class ProgramRepository(private val database: AppDatabase) {

    suspend fun applyTemplate(template: ProgramTemplate): Long {
        val exercisesByName = database.exerciseDao().getAll().first().associateBy { it.name }
        val days = template.days.map { day ->
            EditableDay(
                name = day.name,
                exercises = day.exercises.map { te ->
                    val exercise = requireNotNull(exercisesByName[te.exerciseName]) {
                        "Missing library exercise: ${te.exerciseName}"
                    }
                    EditableExercise(exercise.id, te.sets, te.targetReps, te.increment)
                }
            )
        }
        return createProgram(template.name, template.type, days, activate = true)
    }

    suspend fun createProgram(
        name: String,
        type: ProgramType,
        days: List<EditableDay>,
        activate: Boolean
    ): Long {
        val programId = database.programDao().insert(Program(name = name, type = type, isActive = false))
        val dayEntities = days.mapIndexed { index, day ->
            ProgramDay(programId = programId, name = day.name, orderIndex = index)
        }
        val dayIds = database.programDayDao().insertAll(dayEntities)

        val settings = database.settingsDao().get() ?: SeedData.defaultSettings()
        val programExercises = mutableListOf<ProgramExercise>()
        val workingWeights = LinkedHashMap<Long, WorkingWeight>()

        days.forEachIndexed { dayIndex, day ->
            val dayId = dayIds[dayIndex]
            day.exercises.forEachIndexed { exerciseIndex, exercise ->
                programExercises.add(
                    ProgramExercise(
                        programDayId = dayId,
                        exerciseId = exercise.exerciseId,
                        sets = exercise.sets,
                        targetReps = exercise.targetReps,
                        increment = exercise.increment,
                        orderIndex = exerciseIndex
                    )
                )
                workingWeights.getOrPut(exercise.exerciseId) {
                    WorkingWeight(
                        programId = programId,
                        exerciseId = exercise.exerciseId,
                        weight = settings.barWeight,
                        failStreak = 0
                    )
                }
            }
        }

        database.programExerciseDao().insertAll(programExercises)
        database.workingWeightDao().upsertAll(workingWeights.values.toList())
        if (activate) database.programDao().setActiveProgram(programId)
        return programId
    }

    suspend fun setActive(programId: Long) = database.programDao().setActiveProgram(programId)

    suspend fun delete(programId: Long) = database.programDao().deleteById(programId)
}
