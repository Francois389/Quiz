package com.fsp.quiz.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fsp.quiz.data.ServiceLocator
import com.fsp.quiz.data.quiz.Quiz
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AccueilViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ServiceLocator.getQuizRepository(application)

    val quiz: StateFlow<List<QuizResume>> = repository.observerTousLesQuiz()
        .map { liste -> liste.map { it.versResume() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AccueilViewModel(application) as T
    }
}

private fun Quiz.versResume() = QuizResume(
    id = id,
    titre = titre,
    nbQuestions = nbQuestions,
    dernierScore = dernierScore,
    dernierTotal = dernierTotal
)
