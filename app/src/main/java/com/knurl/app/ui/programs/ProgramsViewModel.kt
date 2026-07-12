package com.knurl.app.ui.programs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.knurl.app.KnurlApplication
import com.knurl.app.data.AppDatabase
import com.knurl.app.data.entity.Program
import com.knurl.app.data.repository.ProgramRepository
import com.knurl.app.data.template.ProgramTemplate
import com.knurl.app.data.template.ProgramTemplates
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProgramsViewModel(
    database: AppDatabase,
    private val programRepository: ProgramRepository
) : ViewModel() {

    val programs: StateFlow<List<Program>> = database.programDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val templates: List<ProgramTemplate> = ProgramTemplates.all

    fun setActive(programId: Long) {
        viewModelScope.launch { programRepository.setActive(programId) }
    }

    fun delete(programId: Long) {
        viewModelScope.launch { programRepository.delete(programId) }
    }

    fun applyTemplate(template: ProgramTemplate) {
        viewModelScope.launch { programRepository.applyTemplate(template) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KnurlApplication
                ProgramsViewModel(app.database, ProgramRepository(app.database))
            }
        }
    }
}
