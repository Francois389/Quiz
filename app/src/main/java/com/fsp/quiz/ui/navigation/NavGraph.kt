package com.fsp.quiz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fsp.quiz.ui.screens.detail.DetailQuizScreen
import com.fsp.quiz.ui.screens.game.JeuScreen
import com.fsp.quiz.ui.screens.home.AccueilScreen
import com.fsp.quiz.ui.screens.results.ResultatsScreen
import com.fsp.quiz.ui.screens.settings.ReglagesScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Accueil.route) {

        composable(Routes.Accueil.route) {
            AccueilScreen(
                onOuvrirQuiz = { quiz -> navController.navigate(Routes.Detail.avec(quiz.id)) },
                onOuvrirReglages = { navController.navigate(Routes.Reglages.route) }
            )
        }

        composable(Routes.Reglages.route) {
            ReglagesScreen(onRetour = { navController.popBackStack() })
        }

        composable(
            route = Routes.Detail.route,
            arguments = listOf(navArgument("quizId") { type = NavType.LongType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getLong("quizId") ?: 0L
            DetailQuizScreen(
                quizId = quizId,
                onDemarrer = { navController.navigate(Routes.Jeu.avec(quizId)) },
                onRetour = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.Jeu.route,
            arguments = listOf(navArgument("quizId") { type = NavType.LongType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getLong("quizId") ?: 0L
            JeuScreen(
                quizId = quizId,
                onTermine = { score, total ->
                    navController.navigate(Routes.Resultats.avec(quizId, score, total)) {
                        popUpTo(Routes.Accueil.route)
                    }
                }
            )
        }

        composable(
            route = Routes.Resultats.route,
            arguments = listOf(
                navArgument("quizId") { type = NavType.LongType },
                navArgument("score") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments
            ResultatsScreen(
                quizId = args?.getLong("quizId") ?: 0L,
                score = args?.getInt("score") ?: 0,
                total = args?.getInt("total") ?: 0,
                onRetourAccueil = {
                    navController.navigate(Routes.Accueil.route) {
                        popUpTo(Routes.Accueil.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
