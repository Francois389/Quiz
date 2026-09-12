package com.fsp.quiz.ui.screens.results


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class RevisionQuestion(
    val texte: String,
    val reponseDonnee: String,
    val bonneReponse: String,
    val correcte: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultatsScreen(
    quizId: Long,
    score: Int,
    total: Int,
    onRetourAccueil: () -> Unit,
    onRecommencer: (() -> Unit)? = null,
    revision: List<RevisionQuestion> = emptyList() // TODO: brancher sur le ViewModel
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Résultats") }) }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (onRecommencer != null) {
                    OutlinedButton(onClick = onRecommencer, modifier = Modifier.weight(1f)) {
                        Text("Recommencer")
                    }
                }
                Button(onClick = onRetourAccueil, modifier = Modifier.weight(1f)) {
                    Text("Retour à l'accueil")
                }
            }
        }
    }
}

@Composable
private fun RevisionCard(q: RevisionQuestion) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = if (q.correcte) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(q.texte, style = MaterialTheme.typography.bodyLarge)
                Text("Ta réponse : ${q.reponseDonnee}", style = MaterialTheme.typography.bodySmall)
                if (!q.correcte) {
                    Text(
                        "Bonne réponse : ${q.bonneReponse}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
