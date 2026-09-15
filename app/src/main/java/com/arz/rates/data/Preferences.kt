package com.arz.rates.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

private val Context.dataStore by preferencesDataStore("arz_preferences")

enum class ThemeMode { SYSTEM, LIGHT, DARK }

class PreferencesRepository(private val context: Context) {
    private val selectedKey = stringPreferencesKey("selected_rates")
    private val themeKey = stringPreferencesKey("theme")
    private val languageKey = stringPreferencesKey("language")

    val selected: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[selectedKey] ?: "[]"
        runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { arr.getString(it) }
        }.getOrDefault(emptyList())
    }

    suspend fun saveSelected(keys: List<String>) {
        val arr = JSONArray()
        keys.distinct().forEach(arr::put)
        context.dataStore.edit { it[selectedKey] = arr.toString() }
    }

    val theme: Flow<ThemeMode> = context.dataStore.data.map {
        when (it[themeKey]?.lowercase()) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    suspend fun saveTheme(theme: ThemeMode) {
        context.dataStore.edit { it[themeKey] = theme.name.lowercase() }
    }

    val language: Flow<Language> = context.dataStore.data.map {
        if (it[languageKey] == "fa") Language.PERSIAN else Language.ENGLISH
    }

    suspend fun saveLanguage(language: Language) {
        context.dataStore.edit { it[languageKey] = if (language == Language.PERSIAN) "fa" else "en" }
    }
}
