package com.fsp.quiz.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fsp.quiz.data.ServiceLocator
import com.fsp.quiz.data.settings.AppSettings
import com.fsp.quiz.data.settings.ModeChrono
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReglagesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ServiceLocator.getSettingsRepository(application)

    val settings: StateFlow<AppSettings> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun setAfficherReponseImmediate(valeur: Boolean) {
        viewModelScope.launch { repository.setAfficherReponseImmediate(valeur) }
    }

    fun setModeChrono(mode: ModeChrono) {
        viewModelScope.launch { repository.setModeChrono(mode) }
    }

    fun setDureeSecondes(secondes: Int) {
        viewModelScope.launch { repository.setDureeSecondes(secondes) }
    }

    fun dureeMiniSuggeree(mode: ModeChrono, nbQuestions: Int): Int =
        repository.dureeMiniSuggeree(mode, nbQuestions)

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ReglagesViewModel(application) as T
    }
}
