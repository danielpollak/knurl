package com.knurl.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.knurl.app.data.entity.Program
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramDao {
    @Query("SELECT * FROM programs ORDER BY name ASC")
    fun getAll(): Flow<List<Program>>

    @Query("SELECT * FROM programs WHERE isActive = 1 LIMIT 1")
    fun getActive(): Flow<Program?>

    @Query("SELECT * FROM programs WHERE id = :id")
    suspend fun getById(id: Long): Program?

    @Insert
    suspend fun insert(program: Program): Long

    @Query("UPDATE programs SET isActive = 0")
    suspend fun deactivateAll()

    @Query("UPDATE programs SET isActive = 1 WHERE id = :id")
    suspend fun activate(id: Long)

    @Transaction
    suspend fun setActiveProgram(id: Long) {
        deactivateAll()
        activate(id)
    }

    @Query("DELETE FROM programs WHERE id = :id")
    suspend fun deleteById(id: Long)
}
