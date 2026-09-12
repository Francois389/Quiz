package com.fsp.quiz.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailQuizScreen(
    quizId: Long,
    onDemarrer: () -> Unit,
    onRetour: () -> Unit,
    viewModel: DetailQuizViewModel = viewModel(
        factory = DetailQuizViewModel.Factory(
            LocalContext.current.applicationContext as android.app.Application,
            quizId
        )
    )
) {
    val quiz by viewModel.quiz.collectAsState()

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
            Text(quiz?.titre ?: "Chargement…", style = MaterialTheme.typography.headlineSmall)
            quiz?.description?.let { Text(it) }
            Text("Nombre de questions : ${quiz?.nbQuestions ?: "—"}")
            Text(
                if (quiz?.dernierScore != null) "Dernier score : ${quiz?.dernierScore}/${quiz?.dernierTotal}"
                else "Dernier score : —"
            )

            Spacer(Modifier.weight(1f))

            Button(onClick = onDemarrer, modifier = Modifier.fillMaxWidth(), enabled = quiz != null) {
                Text("Démarrer")
            }
        }
    }
}
