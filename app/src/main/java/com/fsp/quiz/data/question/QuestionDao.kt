package com.fsp.quiz.data.question

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QuestionDao {

    @Query("SELECT * FROM question WHERE quizId = :quizId ORDER BY ordre ASC")
    suspend fun getQuestionsDuQuiz(quizId: Long): List<Question>

    @Insert
    suspend fun insererTout(questions: List<Question>)

    @Query("SELECT COUNT(*) FROM question WHERE quizId = :quizId")
    suspend fun compterQuestions(quizId: Long): Int
}
