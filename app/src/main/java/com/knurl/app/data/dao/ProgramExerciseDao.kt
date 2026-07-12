package com.knurl.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.knurl.app.data.entity.ProgramExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramExerciseDao {
    @Query("SELECT * FROM program_exercises WHERE programDayId = :programDayId ORDER BY orderIndex ASC")
    fun getByProgramDay(programDayId: Long): Flow<List<ProgramExercise>>

    @Query("SELECT * FROM program_exercises")
    suspend fun getAllRows(): List<ProgramExercise>

    @Insert
    suspend fun insert(programExercise: ProgramExercise): Long

    @Insert
    suspend fun insertAll(programExercises: List<ProgramExercise>): List<Long>

    @Query("DELETE FROM program_exercises WHERE id = :id")
    suspend fun deleteById(id: Long)
}
