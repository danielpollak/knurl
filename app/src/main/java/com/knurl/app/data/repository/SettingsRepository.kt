package com.knurl.app.data.repository

import androidx.room.withTransaction
import com.knurl.app.data.AppDatabase
import com.knurl.app.data.model.WeightUnit
import com.knurl.app.data.seed.SeedData
import com.knurl.app.domain.UnitConversion
import com.knurl.app.domain.WeightMath
import kotlinx.coroutines.flow.first

class SettingsRepository(private val database: AppDatabase) {

    /**
     * Switches the active unit, converting every stored weight (working weights and historical
     * set logs) so the whole dataset stays in a single consistent unit, per the brief: weights
     * are stored in whichever unit is currently selected, not silently kept in a canonical unit.
     * Bar weight and available plates reset to that unit's standard set rather than being
     * mathematically converted, since plates are a fixed physical inventory per unit system.
     */
    suspend fun switchUnit(newUnit: WeightUnit) {
        database.withTransaction {
            val settings = database.settingsDao().get() ?: SeedData.defaultSettings()
            if (settings.unit == newUnit) return@withTransaction

            val newBarWeight = UnitConversion.defaultBarWeight(newUnit)
            val newPlates = UnitConversion.defaultPlates(newUnit)

            val convertedWorkingWeights = database.workingWeightDao().getAllRows().map { workingWeight ->
                val converted = UnitConversion.convert(workingWeight.weight, settings.unit, newUnit)
                workingWeight.copy(weight = WeightMath.roundDownToLoadable(converted, newBarWeight, newPlates))
            }
            database.workingWeightDao().upsertAll(convertedWorkingWeights)

            val convertedSetLogs = database.setLogDao().getAll().first().map { setLog ->
                setLog.copy(weight = UnitConversion.roundToHalf(UnitConversion.convert(setLog.weight, settings.unit, newUnit)))
            }
            database.setLogDao().updateAll(convertedSetLogs)

            database.settingsDao().upsert(
                settings.copy(unit = newUnit, barWeight = newBarWeight, availablePlates = newPlates)
            )
        }
    }

    suspend fun updateBarAndPlates(barWeight: Double, plates: List<Double>) {
        val settings = database.settingsDao().get() ?: SeedData.defaultSettings()
        database.settingsDao().upsert(settings.copy(barWeight = barWeight, availablePlates = plates))
    }

    suspend fun updateRestTimer(seconds: Int) {
        val settings = database.settingsDao().get() ?: SeedData.defaultSettings()
        database.settingsDao().upsert(settings.copy(restTimerSeconds = seconds))
    }
}
