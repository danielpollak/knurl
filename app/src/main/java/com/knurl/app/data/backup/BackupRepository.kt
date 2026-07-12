package com.knurl.app.data.backup

import androidx.room.withTransaction
import com.knurl.app.data.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BackupRepository(private val database: AppDatabase) {

    private val json = Json { prettyPrint = true }

    suspend fun exportJson(): String {
        val data = BackupData(
            exercises = database.exerciseDao().getAll().first(),
            programs = database.programDao().getAll().first(),
            programDays = database.programDayDao().getAllRows(),
            programExercises = database.programExerciseDao().getAllRows(),
            workingWeights = database.workingWeightDao().getAllRows(),
            workoutSessions = database.workoutSessionDao().getAll().first(),
            setLogs = database.setLogDao().getAll().first(),
            settings = database.settingsDao().get()
        )
        return json.encodeToString(data)
    }

    /**
     * Replaces every row in the database with the contents of [content]. Rows are reinserted
     * with their original ids (parents before children) so foreign keys stay valid, making the
     * round trip lossless.
     */
    suspend fun importJson(content: String) {
        val data: BackupData = json.decodeFromString(content)
        database.withTransaction {
            database.clearAllTables()

            database.exerciseDao().insertAll(data.exercises)
            database.programDao().insertAll(data.programs)
            database.programDayDao().insertAll(data.programDays)
            database.programExerciseDao().insertAll(data.programExercises)
            database.workingWeightDao().upsertAll(data.workingWeights)
            data.workoutSessions.forEach { database.workoutSessionDao().insert(it) }
            database.setLogDao().insertAll(data.setLogs)
            data.settings?.let { database.settingsDao().upsert(it) }
        }
    }
}
