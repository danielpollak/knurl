package com.knurl.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.knurl.app.data.entity.SetLog
import kotlinx.coroutines.flow.Flow

@Dao
interface SetLogDao {
    @Query("SELECT * FROM set_logs WHERE sessionId = :sessionId ORDER BY setNumber ASC")
    fun getBySession(sessionId: Long): Flow<List<SetLog>>

    @Query("SELECT * FROM set_logs ORDER BY id ASC")
    fun getAll(): Flow<List<SetLog>>

    @Query(
        "SELECT MAX(ws.epochDate) FROM set_logs sl " +
            "INNER JOIN workout_sessions ws ON ws.id = sl.sessionId " +
            "WHERE sl.exerciseId = :exerciseId AND ws.programId = :programId AND ws.completed = 1"
    )
    suspend fun getLastPerformedEpochDate(programId: Long, exerciseId: Long): Long?

    @Insert
    suspend fun insert(setLog: SetLog): Long

    @Insert
    suspend fun insertAll(setLogs: List<SetLog>): List<Long>
}
