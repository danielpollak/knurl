package com.knurl.app.data.repository

import com.knurl.app.data.AppDatabase
import com.knurl.app.data.seed.SeedData
import java.time.LocalDate
import kotlinx.coroutines.flow.first

class CsvExportRepository(private val database: AppDatabase) {

    private val header = "date,program,day,exercise,muscle_group,weight,unit,set_number,target_reps,reps_done,success"

    suspend fun buildCsv(): String {
        val settings = database.settingsDao().get() ?: SeedData.defaultSettings()
        val programsById = database.programDao().getAll().first().associateBy { it.id }
        val sessionsById = database.workoutSessionDao().getAll().first().associateBy { it.id }
        val setLogs = database.setLogDao().getAll().first()
            .sortedWith(compareBy({ sessionsById[it.sessionId]?.epochDate ?: 0L }, { it.sessionId }, { it.setNumber }))

        val builder = StringBuilder(header).append('\n')
        setLogs.forEach { setLog ->
            val session = sessionsById[setLog.sessionId]
            val programName = session?.let { programsById[it.programId]?.name } ?: ""
            val date = session?.let { LocalDate.ofEpochDay(it.epochDate).toString() } ?: ""
            val dayName = session?.dayName ?: ""

            val row = listOf(
                date,
                programName,
                dayName,
                setLog.exerciseName,
                setLog.muscleGroup.name,
                setLog.weight.toString(),
                settings.unit.name,
                setLog.setNumber.toString(),
                setLog.targetReps.toString(),
                setLog.repsDone.toString(),
                setLog.success.toString()
            ).joinToString(",") { escapeCsvField(it) }

            builder.append(row).append('\n')
        }
        return builder.toString()
    }

    private fun escapeCsvField(field: String): String =
        if (field.contains(',') || field.contains('"') || field.contains('\n')) {
            "\"" + field.replace("\"", "\"\"") + "\""
        } else {
            field
        }
}
