package com.fsp.quiz.data.quiz

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {

    @Query("SELECT * FROM quiz ORDER BY dateImport DESC")
    fun observerTousLesQuiz(): Flow<List<Quiz>>

    @Query("SELECT * FROM quiz WHERE id = :quizId")
    suspend fun getQuizParId(quizId: Long): Quiz?

    @Insert
    suspend fun inserer(quiz: Quiz): Long

    @Update
    suspend fun mettreAJour(quiz: Quiz)

    @Delete
    suspend fun supprimer(quiz: Quiz)

    @Query("UPDATE quiz SET dernierScore = :score, dernierTotal = :total WHERE id = :quizId")
    suspend fun enregistrerScore(quizId: Long, score: Int, total: Int)
}
