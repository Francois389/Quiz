package com.fsp.quiz.ui.screens.game


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Écran de jeu. La logique réelle (chargement des questions, chrono, calcul du score)
 * sera portée par un ViewModel dédié ; ceci est le squelette d'UI.
 *
 * Layout en grille 2 colonnes pour les 4 réponses : plus adapté à une tablette
 * qu'une liste verticale de boutons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JeuScreen(
    quizId: Long,
    onTermine: (score: Int, total: Int) -> Unit
) {
    // TODO: remplacer par l'état réel venant du ViewModel
    var reponseSelectionnee by remember { mutableStateOf<String?>(null) }
    val questionActuelle = "Quelle est la capitale de la France ?"
    val reponses = listOf("Paris", "Lyon", "Marseille", "Toulouse")
    val numeroQuestion = 1
    val totalQuestions = 10

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Question $numeroQuestion / $totalQuestions") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            LinearProgressIndicator(
                progress = { numeroQuestion / totalQuestions.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )

            Text(questionActuelle, style = MaterialTheme.typography.headlineSmall)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(reponses) { reponse ->
                    OutlinedButton(
                        onClick = { reponseSelectionnee = reponse },
                        modifier = Modifier.fillMaxWidth().height(72.dp)
                    ) {
                        Text(reponse)
                    }
                }
            }

            // Bouton "Suivant" visible uniquement après sélection
            // (correspond au réglage "affichage immédiat de la réponse")
            if (reponseSelectionnee != null) {
                Button(
                    onClick = {
                        // TODO: passer à la question suivante, ou terminer le quiz
                        onTermine(0, totalQuestions)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Suivant")
                }
            }
        }
    }
}
