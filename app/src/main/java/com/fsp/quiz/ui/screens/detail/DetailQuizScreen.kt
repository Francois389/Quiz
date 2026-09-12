package com.fsp.quiz.ui.screens.detail


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailQuizScreen(
    quizId: Long,
    onDemarrer: () -> Unit,
    onRetour: () -> Unit
    // TODO: injecter le ViewModel pour charger titre/description/nbQuestions/dernierScore
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détail du questionnaire") },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
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
            Text("Titre du questionnaire", style = MaterialTheme.typography.headlineSmall)
            Text("Description éventuelle du questionnaire.")
            Text("Nombre de questions : —")
            Text("Dernier score : —")

            Spacer(Modifier.weight(1f))

            Button(onClick = onDemarrer, modifier = Modifier.fillMaxWidth()) {
                Text("Démarrer")
            }
        }
    }
}
