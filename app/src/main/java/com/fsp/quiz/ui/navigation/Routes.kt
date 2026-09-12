package com.fsp.quiz.ui.navigation

sealed class Routes(val route: String) {
    object Accueil : Routes("accueil")
    object Reglages : Routes("reglages")

    object Detail : Routes("detail/{quizId}") {
        fun avec(quizId: Long) = "detail/$quizId"
    }

    object Jeu : Routes("jeu/{quizId}") {
        fun avec(quizId: Long) = "jeu/$quizId"
    }

    object Resultats : Routes("resultats/{quizId}/{score}/{total}") {
        fun avec(quizId: Long, score: Int, total: Int) = "resultats/$quizId/$score/$total"
    }
}
