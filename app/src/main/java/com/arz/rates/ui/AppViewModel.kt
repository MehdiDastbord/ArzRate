package com.arz.rates.ui

import android.app.Application
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arz.rates.data.Language
import com.arz.rates.data.PreferencesRepository
import com.arz.rates.data.RateItem
import com.arz.rates.data.RatesRepository
import com.arz.rates.data.ThemeMode
import com.arz.rates.data.WidgetCache
import com.arz.rates.widget.RatesWidget
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class AppState(
    val rates: List<RateItem> = emptyList(),
    val selected: List<String> = emptyList(),
    val language: Language = Language.ENGLISH,
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val loading: Boolean = false,
    val error: String? = null,
    val lastUpdated: Long? = null
)

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val ratesRepo = RatesRepository()
    private val prefs = PreferencesRepository(app)
    private val application = app

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.selected.collect { selected ->
                _state.value = _state.value.copy(selected = selected)
                RatesWidget().updateAll(application)
            }
        }
        viewModelScope.launch {
            prefs.language.collect { language ->
                _state.value = _state.value.copy(language = language)
                RatesWidget().updateAll(application)
            }
        }
        viewModelScope.launch {
            prefs.theme.collect { theme ->
                _state.value = _state.value.copy(theme = theme)
                RatesWidget().updateAll(application)
            }
        }
        refresh()
        viewModelScope.launch {
            while (isActive) {
                delay(60_000)
                refresh()
            }
        }
    }

    fun refresh() {
        if (_state.value.loading) return
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { ratesRepo.fetch() }
                .onSuccess { rates ->
                    WidgetCache.saveRates(application, rates)
                    _state.value = _state.value.copy(
                        rates = rates,
                        loading = false,
                        lastUpdated = System.currentTimeMillis()
                    )
                    RatesWidget().updateAll(application)
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        loading = false,
                        error = e.message ?: "Unable to update rates."
                    )
                    RatesWidget().updateAll(application)
                }
        }
    }

    fun toggle(key: String) {
        viewModelScope.launch {
            val current = _state.value.selected.toMutableList()
            if (key in current) current.remove(key) else current.add(key)
            prefs.saveSelected(current)
            RatesWidget().updateAll(application)
        }
    }

    fun remove(key: String) = toggle(key)

    fun reorder(from: Int, to: Int) {
        viewModelScope.launch {
            val list = _state.value.selected.toMutableList()
            if (from !in list.indices || to !in list.indices || from == to) return@launch
            val item = list.removeAt(from)
            list.add(to.coerceIn(0, list.size), item)
            prefs.saveSelected(list)
            RatesWidget().updateAll(application)
        }
    }

    fun setSelected(keys: List<String>) {
        viewModelScope.launch {
            prefs.saveSelected(keys.distinct())
            RatesWidget().updateAll(application)
        }
    }

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            prefs.saveLanguage(language)
            RatesWidget().updateAll(application)
        }
    }

    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch {
            prefs.saveTheme(theme)
            RatesWidget().updateAll(application)
        }
    }
}
