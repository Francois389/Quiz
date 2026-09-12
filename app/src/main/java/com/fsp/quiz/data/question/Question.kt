package com.fsp.quiz.data.question

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.fsp.quiz.data.quiz.Quiz

/**
 * Une question appartenant à un quiz : une bonne réponse, trois mauvaises.
 */
@Entity(
    tableName = "question",
    foreignKeys = [
        ForeignKey(
            entity = Quiz::class,
            parentColumns = ["id"],
            childColumns = ["quizId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val quizId: Long,
    val texte: String,
    val bonneReponse: String,
    val ordre: Int = 0,
    val mauvaisesReponses: List<String> = emptyList()
) {


    fun toutesLesReponses(): List<String> =
        listOf(bonneReponse) + mauvaisesReponses
}
