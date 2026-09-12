package com.fsp.quiz.data

import android.content.Context
import com.fsp.quiz.data.local.AppDatabase
import com.fsp.quiz.data.quiz.QuizRepository
import com.fsp.quiz.data.settings.SettingsRepository

/**
 * Fournisseur simple de dépendances (pas de Hilt pour l'instant : projet étudiant,
 * on garde ça lisible). À remplacer par Hilt plus tard si le projet grossit.
 */
object ServiceLocator {

    @Volatile
    private var quizRepository: QuizRepository? = null

    @Volatile
    private var settingsRepository: SettingsRepository? = null

    fun getQuizRepository(context: Context): QuizRepository =
        quizRepository ?: synchronized(this) {
            quizRepository ?: run {
                val db = AppDatabase.getInstance(context.applicationContext)
                QuizRepository(db.quizDao(), db.questionDao()).also { quizRepository = it }
            }
        }

    fun getSettingsRepository(context: Context): SettingsRepository =
        settingsRepository ?: synchronized(this) {
            settingsRepository ?: SettingsRepository(context.applicationContext).also {
                settingsRepository = it
            }
        }
}