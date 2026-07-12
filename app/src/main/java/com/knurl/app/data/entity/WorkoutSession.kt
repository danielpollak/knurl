package com.knurl.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "workout_sessions",
    foreignKeys = [
        ForeignKey(
            entity = Program::class,
            parentColumns = ["id"],
            childColumns = ["programId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProgramDay::class,
            parentColumns = ["id"],
            childColumns = ["programDayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("programId"), Index("programDayId")]
)
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Days since the epoch (see [java.time.LocalDate.toEpochDay]). */
    val epochDate: Long,
    val programId: Long,
    val programDayId: Long,
    val dayName: String,
    val completed: Boolean
)
