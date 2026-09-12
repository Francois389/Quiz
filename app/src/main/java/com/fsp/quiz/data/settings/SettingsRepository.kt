package com.fsp.quiz.data.settings


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "quiz_settings")

enum class ModeChrono {
    AUCUN,
    PAR_QUESTION,
    PAR_QUESTIONNAIRE
}

data class AppSettings(
    val afficherReponseImmediate: Boolean = true,
    val modeChrono: ModeChrono = ModeChrono.AUCUN,
    val dureeSecondes: Int = 30
)

/**
 * Réglages globaux de l'application (pas de personnalisation par questionnaire).
 */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val AFFICHER_REPONSE_IMMEDIATE = booleanPreferencesKey("afficher_reponse_immediate")
        val MODE_CHRONO = stringPreferencesKey("mode_chrono")
        val DUREE_SECONDES = intPreferencesKey("duree_secondes")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            afficherReponseImmediate = prefs[Keys.AFFICHER_REPONSE_IMMEDIATE] ?: true,
            modeChrono = prefs[Keys.MODE_CHRONO]?.let {
                runCatching { ModeChrono.valueOf(it) }.getOrDefault(ModeChrono.AUCUN)
            } ?: ModeChrono.AUCUN,
            dureeSecondes = prefs[Keys.DUREE_SECONDES] ?: 30
        )
    }

    suspend fun setAfficherReponseImmediate(valeur: Boolean) {
        context.dataStore.edit { it[Keys.AFFICHER_REPONSE_IMMEDIATE] = valeur }
    }

    suspend fun setModeChrono(mode: ModeChrono) {
        context.dataStore.edit { it[Keys.MODE_CHRONO] = mode.name }
    }

    suspend fun setDureeSecondes(secondes: Int) {
        context.dataStore.edit { it[Keys.DUREE_SECONDES] = secondes }
    }

    /**
     * Durée minimale suggérée en secondes selon le nombre de questions et le mode.
     * Valeurs de départ à ajuster plus tard.
     */
    fun dureeMiniSuggeree(mode: ModeChrono, nbQuestions: Int): Int = when (mode) {
        ModeChrono.PAR_QUESTION -> 10
        ModeChrono.PAR_QUESTIONNAIRE -> (nbQuestions * 10).coerceAtLeast(30)
        ModeChrono.AUCUN -> 0
    }
}
