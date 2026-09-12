package com.fsp.quiz.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fsp.quiz.data.quiz.Quiz

data class QuizResume(
    val id: Long,
    val titre: String,
    val nbQuestions: Int,
    val dernierScore: Int?,
    val dernierTotal: Int?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccueilScreen(
    onOuvrirQuiz: (QuizResume) -> Unit,
    onOuvrirReglages: () -> Unit,
    quiz: List<QuizResume> = emptyList() // TODO: brancher sur le ViewModel + repository
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes questionnaires") },
                actions = {
                    IconButton(onClick = onOuvrirReglages) {
                        Icon(Icons.Default.Settings, contentDescription = "Réglages")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: import de fichier (plus tard) */ }) {
                Icon(Icons.Default.Add, contentDescription = "Importer un questionnaire")
            }
        }
    ) { padding ->
        if (quiz.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucun questionnaire pour l'instant. Importe-en un avec le bouton +.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(quiz) { q ->
                    QuizCard(quiz = q, onClick = { onOuvrirQuiz(q) })
                }
            }
        }
    }
}

@Composable
private fun QuizCard(quiz: QuizResume, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(quiz.titre, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text("${quiz.nbQuestions} questions", style = MaterialTheme.typography.bodyMedium)
            if (quiz.dernierScore != null && quiz.dernierTotal != null) {
                Text(
                    "Dernier score : ${quiz.dernierScore}/${quiz.dernierTotal}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
