package com.arz.rates.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object WidgetCache {
    private const val PREFS = "arz_widget_cache"
    private const val RATES = "rates"

    private val json = Json { ignoreUnknownKeys = true }

    fun saveRates(context: Context, rates: List<RateItem>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(RATES, json.encodeToString(rates))
            .apply()
    }

    fun readRates(context: Context): List<RateItem> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(RATES, null) ?: return emptyList()
        return runCatching { json.decodeFromString<List<RateItem>>(raw) }
            .getOrDefault(emptyList())
    }
}
