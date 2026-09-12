package com.fsp.quiz.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fsp.quiz.data.settings.ModeChrono

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReglagesScreen(
    onRetour: () -> Unit
    // TODO: injecter SettingsRepository / ViewModel pour lire-écrire les vraies valeurs
) {
    var afficherReponseImmediate by remember { mutableStateOf(true) }
    var modeChrono by remember { mutableStateOf(ModeChrono.AUCUN) }
    var dureeSecondes by remember { mutableStateOf("30") }

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
                    checked = afficherReponseImmediate,
                    onCheckedChange = { afficherReponseImmediate = it }
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
                                selected = modeChrono == mode,
                                onClick = { modeChrono = mode }
                            )
                    ) {
                        RadioButton(selected = modeChrono == mode, onClick = { modeChrono = mode })
                        Spacer(Modifier.width(8.dp))
                        Text(libelleMode(mode))
                    }
                }
            }

            if (modeChrono != ModeChrono.AUCUN) {
                OutlinedTextField(
                    value = dureeSecondes,
                    onValueChange = { dureeSecondes = it.filter { c -> c.isDigit() } },
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
