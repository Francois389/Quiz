package com.fsp.quiz.data.quiz

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Un questionnaire importé.
 * dernierScore / dernierTotal permettent d'afficher le dernier résultat
 * sans conserver un historique complet des tentatives.
 */
@Entity(tableName = "quiz")
data class Quiz(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titre: String,
    val description: String? = null,
    val dateImport: Long = System.currentTimeMillis(),
    val nbQuestions: Int = 0,
    val dernierScore: Int? = null,
    val dernierTotal: Int? = null
)
