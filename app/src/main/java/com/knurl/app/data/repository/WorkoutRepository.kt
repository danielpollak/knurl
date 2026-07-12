package com.knurl.app.data.repository

import com.knurl.app.data.AppDatabase
import com.knurl.app.data.entity.SetLog
import com.knurl.app.data.entity.WorkingWeight
import com.knurl.app.data.entity.WorkoutSession
import com.knurl.app.data.seed.SeedData
import com.knurl.app.domain.Progression
import com.knurl.app.domain.WeightMath
import com.knurl.app.domain.WorkingWeightState
import java.time.LocalDate
import kotlinx.coroutines.flow.first

class WorkoutRepository(private val database: AppDatabase) {

    /**
     * Loads the next scheduled day for the active program, applying the staleness deload
     * rule (per exercise) as a display-only adjustment -- nothing is persisted until
     * [finishWorkout] is called, so reopening this screen repeatedly is idempotent.
     */
    suspend fun loadTodaysWorkout(): TodaysWorkout? {
        val program = database.programDao().getActive().first() ?: return null
        val days = database.programDayDao().getByProgram(program.id).first()
        if (days.isEmpty()) return null

        val lastSession = database.workoutSessionDao().getLastCompletedForProgram(program.id)
        val lastDayIndex = lastSession?.let { session -> days.indexOfFirst { it.id == session.programDayId } }
        val nextDay = if (lastDayIndex == null || lastDayIndex == -1) {
            days.first()
        } else {
            days[(lastDayIndex + 1) % days.size]
        }

        val settings = database.settingsDao().get() ?: SeedData.defaultSettings()
        val programExercises = database.programExerciseDao().getByProgramDay(nextDay.id).first()
        val todayEpochDate = LocalDate.now().toEpochDay()

        val exercises = programExercises.sortedBy { it.orderIndex }.map { programExercise ->
            val exercise = requireNotNull(database.exerciseDao().getById(programExercise.exerciseId)) {
                "Missing exercise ${programExercise.exerciseId}"
            }
            // Falls back to an empty bar if this exercise has never been assigned a working
            // weight in this program (e.g. a program created outside the normal flow).
            val stored = database.workingWeightDao().get(program.id, programExercise.exerciseId)
                ?: WorkingWeight(program.id, programExercise.exerciseId, weight = settings.barWeight, failStreak = 0)
            val lastPerformedEpochDate =
                database.setLogDao().getLastPerformedEpochDate(program.id, programExercise.exerciseId)

            val starting = Progression.startingWeight(
                storedWeight = stored.weight,
                storedFailStreak = stored.failStreak,
                lastPerformedEpochDate = lastPerformedEpochDate,
                todayEpochDate = todayEpochDate,
                barWeight = settings.barWeight,
                availablePlates = settings.availablePlates
            )

            WorkoutExerciseUi(
                programExerciseId = programExercise.id,
                exerciseId = exercise.id,
                name = exercise.name,
                muscleGroup = exercise.muscleGroup,
                sets = programExercise.sets,
                targetReps = programExercise.targetReps,
                increment = programExercise.increment,
                startingWeight = starting.weight,
                startingFailStreak = starting.failStreak,
                plateBreakdown = WeightMath.perSidePlates(starting.weight, settings.barWeight, settings.availablePlates)
            )
        }

        return TodaysWorkout(
            programId = program.id,
            programName = program.name,
            programDayId = nextDay.id,
            dayName = nextDay.name,
            epochDate = todayEpochDate,
            unit = settings.unit,
            barWeight = settings.barWeight,
            availablePlates = settings.availablePlates,
            restTimerSeconds = settings.restTimerSeconds,
            exercises = exercises
        )
    }

    /**
     * Writes the [SetLog]s for [workout], applies progression per exercise, and marks the
     * session complete. [repsPerExercise] maps exerciseId to the reps recorded for each set,
     * in set order; exercises missing from the map are assumed to have hit every target rep.
     */
    suspend fun finishWorkout(workout: TodaysWorkout, repsPerExercise: Map<Long, List<Int>>) {
        val sessionId = database.workoutSessionDao().insert(
            WorkoutSession(
                epochDate = workout.epochDate,
                programId = workout.programId,
                programDayId = workout.programDayId,
                dayName = workout.dayName,
                completed = true
            )
        )

        val setLogs = mutableListOf<SetLog>()
        val newWorkingWeights = mutableListOf<WorkingWeight>()

        workout.exercises.forEach { exercise ->
            val reps = repsPerExercise[exercise.exerciseId] ?: List(exercise.sets) { exercise.targetReps }
            val succeeded = reps.all { it >= exercise.targetReps }

            reps.forEachIndexed { index, repsDone ->
                setLogs.add(
                    SetLog(
                        sessionId = sessionId,
                        exerciseId = exercise.exerciseId,
                        exerciseName = exercise.name,
                        muscleGroup = exercise.muscleGroup,
                        weight = exercise.startingWeight,
                        targetReps = exercise.targetReps,
                        setNumber = index + 1,
                        repsDone = repsDone,
                        success = repsDone >= exercise.targetReps
                    )
                )
            }

            val result = Progression.applyResult(
                startingWeight = WorkingWeightState(exercise.startingWeight, exercise.startingFailStreak),
                succeeded = succeeded,
                increment = exercise.increment,
                barWeight = workout.barWeight,
                availablePlates = workout.availablePlates
            )
            newWorkingWeights.add(
                WorkingWeight(
                    programId = workout.programId,
                    exerciseId = exercise.exerciseId,
                    weight = result.weight,
                    failStreak = result.failStreak
                )
            )
        }

        database.setLogDao().insertAll(setLogs)
        database.workingWeightDao().upsertAll(newWorkingWeights)
    }
}
