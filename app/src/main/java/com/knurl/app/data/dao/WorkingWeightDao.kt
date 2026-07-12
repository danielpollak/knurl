package com.knurl.app.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.knurl.app.data.entity.WorkingWeight
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkingWeightDao {
    @Query("SELECT * FROM working_weights WHERE programId = :programId AND exerciseId = :exerciseId")
    fun observe(programId: Long, exerciseId: Long): Flow<WorkingWeight?>

    @Query("SELECT * FROM working_weights WHERE programId = :programId AND exerciseId = :exerciseId")
    suspend fun get(programId: Long, exerciseId: Long): WorkingWeight?

    @Query("SELECT * FROM working_weights WHERE programId = :programId")
    fun getAllForProgram(programId: Long): Flow<List<WorkingWeight>>

    @Query("SELECT * FROM working_weights")
    suspend fun getAllRows(): List<WorkingWeight>

    @Upsert
    suspend fun upsert(workingWeight: WorkingWeight)

    @Upsert
    suspend fun upsertAll(workingWeights: List<WorkingWeight>)
}
