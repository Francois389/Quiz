package com.fsp.quiz.ui.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

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
    onOuvrirQuiz: (Long) -> Unit,
    onOuvrirReglages: () -> Unit,
    viewModel: AccueilViewModel = viewModel(
        factory = AccueilViewModel.Factory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val quiz by viewModel.quiz.collectAsState()
    val contexte = LocalContext.current
    var erreurImport by remember { mutableStateOf<String?>(null) }

    val lanceurImport = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importerFichier(contexte, uri) { erreur ->
                erreurImport = erreur
            }
        }
    }

    erreurImport?.let { message ->
        AlertDialog(
            onDismissRequest = { erreurImport = null },
            confirmButton = { TextButton(onClick = { erreurImport = null }) { Text("OK") } },
            title = { Text("Import impossible") },
            text = { Text(message) }
        )
    }

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
            FloatingActionButton(onClick = { lanceurImport.launch(arrayOf("application/json")) }) {
                Icon(Icons.Default.Add, contentDescription = "Importer un questionnaire")
            }
        }
    ) { padding ->
        if (quiz.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucun questionnaire pour l'instant. Importe-en un avec le bouton +.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(quiz) { q -> QuizCard(quiz = q, onClick = { onOuvrirQuiz(q.id) }) }
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
