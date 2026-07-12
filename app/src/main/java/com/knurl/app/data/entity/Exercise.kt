package com.knurl.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.knurl.app.data.model.MuscleGroup

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val muscleGroup: MuscleGroup,
    val isCustom: Boolean
)
