package com.fsp.quiz.ui.navigation

sealed class Routes(val route: String) {
    object Accueil : Routes("accueil")
    object Reglages : Routes("reglages")

    object Detail : Routes("detail/{quizId}") {
        fun avec(quizId: Long) = "detail/$quizId"
    }

    /** Sous-graphe englobant Jeu + Résultats : ils partagent la même instance de JeuViewModel. */
    object Session : Routes("session/{quizId}") {
        fun avec(quizId: Long) = "session/$quizId"
    }

    object Jeu : Routes("session/{quizId}/jeu") {
        fun avec(quizId: Long) = "session/$quizId/jeu"
    }

    object Resultats : Routes("session/{quizId}/resultats") {
        fun avec(quizId: Long) = "session/$quizId/resultats"
    }
}
