package com.fsp.quiz.ui.screens.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fsp.quiz.data.ServiceLocator
import com.fsp.quiz.data.quiz.ImportException
import com.fsp.quiz.data.quiz.Quiz
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccueilViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ServiceLocator.getQuizRepository(application)

    val quiz: StateFlow<List<QuizResume>> = repository.observerTousLesQuiz()
        .map { liste -> liste.map { it.versResume() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun importerFichier(contexte: android.content.Context, uri: Uri, surErreur: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val contenu = contexte.contentResolver.openInputStream(uri)?.use { flux ->
                    flux.bufferedReader().readText()
                } ?: throw ImportException("Impossible de lire le fichier sélectionné.")

                repository.importerDepuisJson(contenu)
            } catch (e: ImportException) {
                surErreur(e.message ?: "Format de fichier invalide.")
            } catch (e: Exception) {
                surErreur("Erreur inattendue lors de l'import : ${e.message}")
            }
        }
    }

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
