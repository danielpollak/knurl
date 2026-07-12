package com.knurl.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.knurl.app.data.model.WeightUnit

/** Singleton row: there is only ever one Settings record, at [id]. */
@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val unit: WeightUnit,
    val barWeight: Double,
    val restTimerSeconds: Int,
    val availablePlates: List<Double>
) {
    companion object {
        const val SINGLETON_ID = 0
    }
}
