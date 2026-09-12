package com.fsp.quiz.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import com.fsp.quiz.data.settings.ModeChrono

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReglagesScreen(
    onRetour: () -> Unit,
    viewModel: ReglagesViewModel = viewModel(
        factory = ReglagesViewModel.Factory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val settings by viewModel.settings.collectAsState()
    var dureeTexte by remember(settings.dureeSecondes) { mutableStateOf(settings.dureeSecondes.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Réglages") },
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Afficher la réponse immédiatement")
                Switch(
                    checked = settings.afficherReponseImmediate,
                    onCheckedChange = { viewModel.setAfficherReponseImmediate(it) }
                )
            }

            Column {
                Text("Chronométrage", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                ModeChrono.entries.forEach { mode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = settings.modeChrono == mode,
                                onClick = { viewModel.setModeChrono(mode) }
                            )
                    ) {
                        RadioButton(
                            selected = settings.modeChrono == mode,
                            onClick = { viewModel.setModeChrono(mode) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(libelleMode(mode))
                    }
                }
            }

            if (settings.modeChrono != ModeChrono.AUCUN) {
                OutlinedTextField(
                    value = dureeTexte,
                    onValueChange = { saisie ->
                        dureeTexte = saisie.filter { c -> c.isDigit() }
                        dureeTexte.toIntOrNull()?.let { viewModel.setDureeSecondes(it) }
                    },
                    label = { Text("Durée (secondes)") },
                    supportingText = { Text("Une durée minimale sera imposée selon le nombre de questions.") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun libelleMode(mode: ModeChrono): String = when (mode) {
    ModeChrono.AUCUN -> "Aucun"
    ModeChrono.PAR_QUESTION -> "Par question"
    ModeChrono.PAR_QUESTIONNAIRE -> "Par questionnaire"
}
