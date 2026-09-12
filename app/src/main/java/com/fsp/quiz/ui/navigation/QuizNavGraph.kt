package com.fsp.quiz.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.fsp.quiz.ui.screens.detail.DetailQuizScreen
import com.fsp.quiz.ui.screens.game.JeuScreen
import com.fsp.quiz.ui.screens.game.JeuViewModel
import com.fsp.quiz.ui.screens.home.AccueilScreen
import com.fsp.quiz.ui.screens.results.ResultatsScreen
import com.fsp.quiz.ui.screens.settings.ReglagesScreen

@Composable
fun QuizNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Accueil.route) {

        composable(Routes.Accueil.route) {
            AccueilScreen(
                onOuvrirQuiz = { quizId -> navController.navigate(Routes.Detail.avec(quizId)) },
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
                onDemarrer = { navController.navigate(Routes.Session.avec(quizId)) },
                onRetour = { navController.popBackStack() }
            )
        }

        // Sous-graphe Session : Jeu et Résultats partagent la même instance de JeuViewModel,
        // ce qui permet de transmettre score + révision détaillée sans repasser par la base
        // (on ne conserve pas d'historique complet des tentatives).
        navigation(
            route = Routes.Session.route,
            startDestination = Routes.Jeu.route,
            arguments = listOf(navArgument("quizId") { type = NavType.LongType })
        ) {
            composable(Routes.Jeu.route) { backStackEntry ->
                val quizId = backStackEntry.arguments?.getLong("quizId") ?: 0L
                val sessionEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.Session.avec(quizId))
                }
                val jeuViewModel: JeuViewModel = viewModel(
                    sessionEntry,
                    factory = JeuViewModel.Factory(
                        LocalContext.current.applicationContext as Application,
                        quizId
                    )
                )
                JeuScreen(
                    viewModel = jeuViewModel,
                    onTermine = {
                        navController.navigate(Routes.Resultats.route) {
                            popUpTo(Routes.Jeu.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.Resultats.route) { backStackEntry ->
                val quizId = backStackEntry.arguments?.getLong("quizId") ?: 0L
                val sessionEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.Session.avec(quizId))
                }
                val jeuViewModel: JeuViewModel = viewModel(
                    sessionEntry,
                    factory = JeuViewModel.Factory(
                        LocalContext.current.applicationContext as Application,
                        quizId
                    )
                )
                ResultatsScreen(
                    jeuViewModel = jeuViewModel,
                    onRetourAccueil = {
                        navController.navigate(Routes.Accueil.route) {
                            popUpTo(Routes.Accueil.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
