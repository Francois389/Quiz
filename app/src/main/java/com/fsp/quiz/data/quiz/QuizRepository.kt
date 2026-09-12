package com.fsp.quiz.data.quiz

import com.fsp.quiz.data.question.Question
import com.fsp.quiz.data.question.QuestionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json.Default.decodeFromString

class QuizRepository(
    private val quizDao: QuizDao,
    private val questionDao: QuestionDao
) {
    fun observerTousLesQuiz(): Flow<List<Quiz>> =
        quizDao.observerTousLesQuiz()

    suspend fun getQuiz(quizId: Long): Quiz? =
        quizDao.getQuizParId(quizId)

    suspend fun getQuestions(quizId: Long): List<Question> =
        questionDao.getQuestionsDuQuiz(quizId)

    suspend fun enregistrerScore(quizId: Long, score: Int, total: Int) =
        quizDao.enregistrerScore(quizId, score, total)

    suspend fun supprimerQuiz(quiz: Quiz) = quizDao.supprimer(quiz)

    /**
     * Parse le contenu JSON et insère le questionnaire + ses questions en base.
     * Lève ImportException pour toute erreur de contenu (validation métier),
     * laisse remonter SerializationException pour un JSON malformé.
     */
    suspend fun importerDepuisJson(contenu: String): Long {
        val dto = try {
            decodeFromString<QuizImportDto>(contenu)
        } catch (e: SerializationException) {
            throw ImportException(
                "Le fichier n'est pas un JSON valide ou ne respecte pas le format attendu.",
                e.cause
            )
        }

        if (dto.titre.isBlank()) {
            throw ImportException("Le champ \"titre\" est manquant ou vide.")
        }
        if (dto.questions.isEmpty()) {
            throw ImportException("Le questionnaire ne contient aucune question.")
        }

        dto.questions.forEachIndexed { index, q ->
            if (q.texte.isBlank()) {
                throw ImportException(
                    "Question n°${index + 1} : champ \"question\" manquant.",
                )
            }
            if (q.bonneReponse.isBlank()) {
                throw ImportException(
                    "Question n°${index + 1} : champ \"bonneReponse\" manquant.",
                )
            }
            if (q.mauvaiseReponse.isEmpty()) {
                throw ImportException(
                    "Question n°${index + 1} : aucune mauvaise réponse fournie.",
                )
            }
        }

        val quiz =
            Quiz(titre = dto.titre, description = dto.description, nbQuestions = dto.questions.size)
        println(quiz)
        val quizId = quizDao.inserer(
            quiz
        )

        questionDao.insererTout(
            dto.questions.mapIndexed { index, q ->
                Question(
                    quizId = quizId,
                    texte = q.texte,
                    bonneReponse = q.bonneReponse,
                    mauvaisesReponses = q.mauvaiseReponse,
                    ordre = index
                )
            }
        )

        return quizId
    }
}

class ImportException(message: String, cause: Throwable? = null) : Exception(message, cause)
