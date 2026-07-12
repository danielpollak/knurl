package com.knurl.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.knurl.app.data.entity.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAll(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getById(id: Long): Exercise?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(exercise: Exercise): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(exercises: List<Exercise>): List<Long>

    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun deleteById(id: Long)
}
