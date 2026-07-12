package com.knurl.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.knurl.app.data.entity.ProgramDay
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramDayDao {
    @Query("SELECT * FROM program_days WHERE programId = :programId ORDER BY orderIndex ASC")
    fun getByProgram(programId: Long): Flow<List<ProgramDay>>

    @Query("SELECT * FROM program_days WHERE id = :id")
    suspend fun getById(id: Long): ProgramDay?

    @Insert
    suspend fun insert(day: ProgramDay): Long

    @Insert
    suspend fun insertAll(days: List<ProgramDay>): List<Long>

    @Query("DELETE FROM program_days WHERE id = :id")
    suspend fun deleteById(id: Long)
}
