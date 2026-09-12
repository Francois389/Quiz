package com.fsp.quiz.data.quiz

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuizImportDto(
    val titre: String,
    val description: String? = null,
    val questions: List<QuestionImportDto> = emptyList()
)

@Serializable
data class QuestionImportDto(
    @SerialName("question")
    val texte: String,
    val bonneReponse: String,
    val mauvaiseReponse: List<String> = emptyList()
)