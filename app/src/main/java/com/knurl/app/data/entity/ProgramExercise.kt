package com.knurl.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "program_exercises",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDay::class,
            parentColumns = ["id"],
            childColumns = ["programDayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("programDayId"), Index("exerciseId")]
)
data class ProgramExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val programDayId: Long,
    val exerciseId: Long,
    val sets: Int,
    val targetReps: Int,
    val increment: Double,
    val orderIndex: Int
)
