package com.knurl.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.knurl.app.KnurlApplication
import com.knurl.app.data.AppDatabase
import com.knurl.app.data.backup.BackupRepository
import com.knurl.app.data.entity.Settings
import com.knurl.app.data.model.WeightUnit
import com.knurl.app.data.repository.CsvExportRepository
import com.knurl.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    database: AppDatabase,
    private val settingsRepository: SettingsRepository,
    private val csvExportRepository: CsvExportRepository,
    private val backupRepository: BackupRepository
) : ViewModel() {

    val settings: StateFlow<Settings?> = database.settingsDao().observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun switchUnit(unit: WeightUnit) {
        viewModelScope.launch { settingsRepository.switchUnit(unit) }
    }

    fun updateBarAndPlates(barWeight: Double, plates: List<Double>) {
        viewModelScope.launch { settingsRepository.updateBarAndPlates(barWeight, plates) }
    }

    fun updateRestTimer(seconds: Int) {
        viewModelScope.launch { settingsRepository.updateRestTimer(seconds) }
    }

    suspend fun buildCsv(): String = csvExportRepository.buildCsv()

    suspend fun buildJsonBackup(): String = backupRepository.exportJson()

    suspend fun restoreFromJson(content: String) = backupRepository.importJson(content)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KnurlApplication
                SettingsViewModel(
                    database = app.database,
                    settingsRepository = SettingsRepository(app.database),
                    csvExportRepository = CsvExportRepository(app.database),
                    backupRepository = BackupRepository(app.database)
                )
            }
        }
    }
}
