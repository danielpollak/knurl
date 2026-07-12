package com.knurl.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.knurl.app.data.model.ProgramType

@Entity(tableName = "programs")
data class Program(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: ProgramType,
    val isActive: Boolean
)
