package com.fsp.quiz.ui.screens.game

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fsp.quiz.data.ServiceLocator
import com.fsp.quiz.data.question.Question
import com.fsp.quiz.data.settings.AppSettings
import com.fsp.quiz.data.settings.ModeChrono
import com.fsp.quiz.ui.screens.results.RevisionQuestion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

data class JeuUiState(
    val chargement: Boolean = true,
    val questions: List<Question> = emptyList(),
    val indexQuestion: Int = 0,
    val reponsesAffichees: List<String> = emptyList(),
    val reponseSelectionnee: String? = null,
    val reponseValidee: Boolean = false,
    val termine: Boolean = false,
    // Chrono : null si le mode AUCUN est actif.
    val tempsRestantSecondes: Int? = null,
    val dureeInitialeSecondes: Int? = null,
    val modeChrono: ModeChrono = ModeChrono.AUCUN
) {
    val questionActuelle: Question? get() = questions.getOrNull(indexQuestion)
    val totalQuestions: Int get() = questions.size
}

class JeuViewModel(
    application: Application,
    private val quizId: Long
) : AndroidViewModel(application) {

    private val repository = ServiceLocator.getQuizRepository(application)
    private val settingsRepository = ServiceLocator.getSettingsRepository(application)

    private val _uiState = MutableStateFlow(JeuUiState())
    val uiState: StateFlow<JeuUiState> = _uiState.asStateFlow()

    val settings: StateFlow<AppSettings> = settingsRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    // Réponses données au fil de la partie (question -> réponse donnée, ou null si temps écoulé).
    private val reponsesDonnees = mutableListOf<Pair<Question, String?>>()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val questions = repository.getQuestions(quizId)
            val settingsActuels = settingsRepository.settingsFlow.first()

            _uiState.update {
                it.copy(
                    chargement = false,
                    questions = questions,
                    reponsesAffichees = questions.firstOrNull()?.toutesLesReponses()?.shuffled()
                        ?: emptyList(),
                    modeChrono = settingsActuels.modeChrono
                )
            }

            demarrerChronoSiNecessaire(settingsActuels)
        }
    }

    /** Démarre le bon minuteur selon le mode, une seule fois pour le questionnaire, ou à chaque question. */
    private fun demarrerChronoSiNecessaire(settingsActuels: AppSettings) {
        when (settingsActuels.modeChrono) {
            ModeChrono.AUCUN -> Unit
            ModeChrono.PAR_QUESTIONNAIRE -> demarrerCompteARebours(settingsActuels.dureeSecondes) {
                // Temps écoulé pour tout le questionnaire : on termine directement.
                terminerPartie()
            }

            ModeChrono.PAR_QUESTION -> demarrerCompteARebours(settingsActuels.dureeSecondes) {
                // Temps écoulé pour cette question : on la compte comme fausse et on avance.
                validerEtAvancer(reponse = null)
            }
        }
    }

    private fun demarrerCompteARebours(dureeSecondes: Int, aExpiration: () -> Unit) {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                tempsRestantSecondes = dureeSecondes,
                dureeInitialeSecondes = dureeSecondes
            )
        }
        timerJob = viewModelScope.launch {
            var restant = dureeSecondes
            while (0 < restant) {
                delay(1.seconds)
                restant -= 1
                _uiState.update { it.copy(tempsRestantSecondes = restant) }
            }
            aExpiration()
        }
    }

    /** L'utilisateur sélectionne une réponse. */
    fun selectionnerReponse(reponse: String) {
        if (_uiState.value.reponseValidee) return

        // En mode "par question", valider une réponse arrête le décompte de cette question.
        when (_uiState.value.modeChrono) {
            ModeChrono.PAR_QUESTION -> timerJob?.cancel()
            else -> Unit
        }
        _uiState.update {
            it.copy(
                reponseSelectionnee = reponse,
                reponseValidee = true
            )
        }
    }

    /** Appelé par le bouton "Suivant" (l'utilisateur a déjà sélectionné une réponse). */
    fun passerASuivant() {
        validerEtAvancer(_uiState.value.reponseSelectionnee)
    }

    private fun validerEtAvancer(reponse: String?) {
        val question = _uiState.value.questionActuelle ?: return

        reponsesDonnees.add(question to reponse)

        val indexSuivant = _uiState.value.indexQuestion + 1
        if (_uiState.value.questions.size <= indexSuivant) {
            terminerPartie()
        } else {
            val prochaineQuestion = _uiState.value.questions[indexSuivant]
            _uiState.update {
                it.copy(
                    indexQuestion = indexSuivant,
                    reponsesAffichees = prochaineQuestion.toutesLesReponses().shuffled(),
                    reponseSelectionnee = null,
                    reponseValidee = false
                )
            }
            // En mode "par question", chaque question relance son propre décompte.
            if (_uiState.value.modeChrono == ModeChrono.PAR_QUESTION) {
                demarrerCompteARebours(_uiState.value.dureeInitialeSecondes ?: 30) {
                    validerEtAvancer(reponse = null)
                }
            }
        }
    }

    private fun terminerPartie() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(termine = true)
        val score = calculerScore()
        viewModelScope.launch {
            repository.enregistrerScore(quizId, score, uiState.value.totalQuestions)
        }
    }

    fun calculerScore(): Int =
        reponsesDonnees.count { (question, reponse) -> reponse == question.bonneReponse }

    fun construireRevision(): List<RevisionQuestion> = reponsesDonnees.map { (question, reponse) ->
        RevisionQuestion(
            texte = question.texte,
            reponseDonnee = reponse,
            bonneReponse = question.bonneReponse,
            correcte = reponse == question.bonneReponse
        )
    }

    @SuppressLint("EmptySuperCall")
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    class Factory(
        private val application: Application,
        private val quizId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            JeuViewModel(application, quizId) as T
    }
}