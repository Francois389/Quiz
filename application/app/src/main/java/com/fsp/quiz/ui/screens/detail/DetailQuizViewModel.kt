package com.fsp.quiz.ui.screens.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fsp.quiz.data.ServiceLocator
import com.fsp.quiz.data.quiz.Quiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailQuizViewModel(
    application: Application,
    private val quizId: Long
) : AndroidViewModel(application) {

    private val repository = ServiceLocator.getQuizRepository(application)

    private val _quiz = MutableStateFlow<Quiz?>(null)
    val quiz: StateFlow<Quiz?> = _quiz.asStateFlow()

    init {
        viewModelScope.launch {
            _quiz.value = repository.getQuiz(quizId)
        }
    }

    class Factory(
        private val application: Application,
        private val quizId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetailQuizViewModel(application, quizId) as T
    }
}
