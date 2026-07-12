package com.knurl.app.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.knurl.app.data.entity.Settings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = ${Settings.SINGLETON_ID}")
    fun observe(): Flow<Settings?>

    @Query("SELECT * FROM settings WHERE id = ${Settings.SINGLETON_ID}")
    suspend fun get(): Settings?

    @Upsert
    suspend fun upsert(settings: Settings)
}
