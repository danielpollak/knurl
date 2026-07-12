package com.knurl.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.knurl.app.data.entity.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions ORDER BY epochDate DESC, id DESC")
    fun getAll(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    suspend fun getById(id: Long): WorkoutSession?

    @Query(
        "SELECT * FROM workout_sessions WHERE programId = :programId AND completed = 1 " +
            "ORDER BY epochDate DESC, id DESC LIMIT 1"
    )
    suspend fun getLastCompletedForProgram(programId: Long): WorkoutSession?

    @Insert
    suspend fun insert(session: WorkoutSession): Long

    @Update
    suspend fun update(session: WorkoutSession)
}
