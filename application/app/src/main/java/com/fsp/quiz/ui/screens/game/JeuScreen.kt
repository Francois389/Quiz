package com.fsp.quiz.ui.screens.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fsp.quiz.data.settings.AppSettings
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JeuScreen(
    viewModel: JeuViewModel,
    onTermine: () -> Unit
) {
    val etat by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    LaunchedEffect(etat.termine) {
        if (etat.termine) onTermine()
    }

    if (etat.chargement) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val question = etat.questionActuelle ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Question ${etat.indexQuestion + 1} / ${etat.totalQuestions}") },
                actions = {
                    etat.tempsRestantSecondes?.let { temps ->
                        val tempsTotal = settings.dureeSecondes.seconds
                        Chronometre(
                            tempsTotal = tempsTotal,
                            tempsRestant = temps.seconds,
                            urgencePourcentage = 20
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LinearProgressIndicator(
                progress = { (etat.indexQuestion + 1) / etat.totalQuestions.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                question.texte, style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )

            GrilleReponse(
                reponses = etat.reponsesAffichees,
                bonneReponse = question.bonneReponse,
                reponseSelectionnee = etat.reponseSelectionnee,
                settings = settings,
                onReponseSelectionne = viewModel::selectionnerReponse,
            )

            if (etat.reponseValidee) {
                Button(
                    onClick = viewModel::passerASuivant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (etat.indexQuestion + 1 >= etat.totalQuestions) "Terminer" else "Suivant")
                }
            }
        }
    }
}

@Composable
fun GrilleReponse(
    reponses: List<String>,
    bonneReponse: String,
    reponseSelectionnee: String?,
    settings: AppSettings,
    onReponseSelectionne: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(reponses) { reponse ->
            val estSelectionnee = reponse == reponseSelectionnee
            val estCorrecte = reponse == bonneReponse

            val couleurs = when {
                reponseSelectionnee == null -> ButtonDefaults.outlinedButtonColors()

                settings.afficherReponseImmediate -> when {
                    estCorrecte -> ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    )

                    estSelectionnee -> ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                    )

                    else -> ButtonDefaults.outlinedButtonColors()
                }

                else -> ButtonDefaults.outlinedButtonColors()
            }

            OutlinedButton(
                onClick = { onReponseSelectionne(reponse) },
                enabled = reponseSelectionnee == null,
                colors = couleurs,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                Text(
                    reponse,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (estCorrecte) MaterialTheme.colorScheme.onPrimaryContainer else Color.Unspecified
                )
            }
        }
    }
}

/**
 * Affiche un chronomètre.
 *
 */
@Composable
fun Chronometre(
    tempsTotal: Duration,
    tempsRestant: Duration = tempsTotal,
    modifier: Modifier = Modifier,
    urgencePourcentage: Long = 0,
) {
    val pourcentageRestant = (tempsRestant.inWholeSeconds * 100) / tempsTotal.inWholeSeconds
    val estEnUrgence = urgencePourcentage.coerceIn(0, 100).let {
        it != 0L && pourcentageRestant <= it
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            Icons.Default.Timer,
            contentDescription = "Temps restant",
        )
        Text(
            text = tempsRestant.toString(),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 8.dp),
            color = if (estEnUrgence) MaterialTheme.colorScheme.error else Color.Unspecified,
        )
    }
}

@Preview
@Composable
fun ChronometrePreview() {
    MaterialTheme {
        FlowColumn {
            val tempsTotal = 30.seconds
            (0..tempsTotal.inWholeSeconds).forEach {
                Chronometre(
                    tempsTotal = tempsTotal,
                    tempsRestant = it.seconds,
                    urgencePourcentage = 20
                )
            }
        }
    }
}