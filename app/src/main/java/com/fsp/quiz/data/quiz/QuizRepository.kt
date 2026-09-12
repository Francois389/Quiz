package com.fsp.quiz.data.quiz

import com.fsp.quiz.data.question.Question
import com.fsp.quiz.data.question.QuestionDao
import kotlinx.coroutines.flow.Flow

class QuizRepository(
    private val quizDao: QuizDao,
    private val questionDao: QuestionDao
) {
    fun observerTousLesQuiz(): Flow<List<Quiz>> = quizDao.observerTousLesQuiz()

    suspend fun getQuiz(quizId: Long): Quiz? = quizDao.getQuizParId(quizId)

    suspend fun getQuestions(quizId: Long): List<Question> = questionDao.getQuestionsDuQuiz(quizId)

    suspend fun enregistrerScore(quizId: Long, score: Int, total: Int) =
        quizDao.enregistrerScore(quizId, score, total)

    suspend fun supprimerQuiz(quiz: Quiz) = quizDao.supprimer(quiz)

}
