package com.fsp.quiz.ui.screens.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fsp.quiz.ui.screens.game.JeuViewModel
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

data class RevisionQuestion(
    val texte: String,
    val reponseDonnee: String?,
    val bonneReponse: String,
    val correcte: Boolean
) {

}

enum class ResultatStatut {
    EXCELLENT,
    BON,
    MOYEN,
    UNE_CORRECTE,
    MAUVAIS,
    FAUSSE;
    companion object {
        fun calculer(resultas: List<RevisionQuestion>): ResultatStatut {
            val correctCount = resultas.count { it.correcte }
            val total = resultas.size
            val pourcentageCorrect = (correctCount.toFloat() / total.toFloat()) * 100
            return when {
                correctCount == total -> EXCELLENT
                correctCount == 1 -> UNE_CORRECTE
                pourcentageCorrect >= 50 -> BON
                pourcentageCorrect >= 25 -> MOYEN
                correctCount == 0 -> FAUSSE
                else -> MAUVAIS
            }
        }
    }
}


/**
 * Reçoit le JeuViewModel de la partie qui vient de se terminer (score + révision déjà en mémoire),
 * pas besoin de repasser par la base : on ne conserve pas d'historique complet des tentatives.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultatsScreen(
    jeuViewModel: JeuViewModel,
    onRetourAccueil: () -> Unit
) {
    val score = jeuViewModel.calculerScore()
    val total = jeuViewModel.uiState.collectAsState().value.totalQuestions
    val revision = jeuViewModel.construireRevision()

    LaunchedEffect(revision) {
        val status = ResultatStatut.calculer(revision)
        when(status) {
            ResultatStatut.EXCELLENT -> Party(
                emitter = Emitter(
                    duration = 5,
                    timeUnit = TimeUnit.SECONDS
                ).max(1)
            )
            else -> Unit
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Résultats") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Score : $score / $total",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(revision) { q ->
                    RevisionCard(q)
                }
            }

            Button(
                onClick = onRetourAccueil,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text("Retour à l'accueil")
            }
        }
    }
}

@Composable
private fun RevisionCard(resultat: RevisionQuestion) {
    val couleur = if (resultat.correcte) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    } else {
        CardDefaults.cardColors()
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = couleur
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = if (resultat.correcte) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(resultat.texte, style = MaterialTheme.typography.bodyLarge)
                resultat.reponseDonnee?.let {
                    Text(
                        "Ta réponse : ${resultat.reponseDonnee}",
                        style = MaterialTheme.typography.bodySmall
                    )
                } ?: Text("Pas de réponse", style = MaterialTheme.typography.bodySmall)
                Text(
                    "Bonne réponse : ${resultat.bonneReponse}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
