package com.arz.rates.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.text.Text
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.arz.rates.data.CurrencyNames
import com.arz.rates.data.Language
import com.arz.rates.data.PreferencesRepository
import com.arz.rates.data.RateItem
import com.arz.rates.data.RatesRepository
import com.arz.rates.data.WidgetCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class RatesWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = withContext(Dispatchers.IO) {
            val prefs = PreferencesRepository(context)
            val selected = prefs.selected.first()
            val language = prefs.language.first()
            var rates = WidgetCache.readRates(context)

            // The widget must never depend on the app being opened first.
            // If the cache is empty, fetch the latest data immediately.
            if (rates.isEmpty()) {
                rates = runCatching { RatesRepository().fetch() }.getOrDefault(emptyList())
                if (rates.isNotEmpty()) WidgetCache.saveRates(context, rates)
            }

            WidgetData(
                language = language,
                selected = selected.mapNotNull { key -> rates.find { it.key.equals(key, ignoreCase = true) } }
            )
        }

        provideContent {
            WidgetContent(data)
        }
    }
}

private data class WidgetData(
    val language: Language,
    val selected: List<RateItem>
)

@androidx.compose.runtime.Composable
private fun WidgetContent(data: WidgetData) {
    val isPersian = data.language == Language.PERSIAN
    val title = if (isPersian) "نرخ ارز" else "Arz Rates"
    val empty = if (isPersian) "ارز موردنظر را در برنامه انتخاب کنید." else "Select currencies in the app."

    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(title)

        if (data.selected.isEmpty()) {
            Text(empty)
        } else {
            data.selected.take(4).forEach { item ->
                WidgetRateRow(item, data.language)
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun WidgetRateRow(item: RateItem, language: Language) {
    val name = CurrencyNames.nameFor(item.key, language)
    val value = item.rate.value ?: "—"

    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(name)
            Text(item.key.uppercase())
        }
        Text(value)
    }
}

class RatesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RatesWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        requestImmediateUpdate(context)
        scheduleBackgroundUpdates(context)
    }

    override fun onDisabled(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WIDGET_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(WIDGET_INITIAL_WORK_NAME)
        super.onDisabled(context)
    }

    companion object {
        const val WIDGET_WORK_NAME = "arz_rates_widget_updates"
        const val WIDGET_INITIAL_WORK_NAME = "arz_rates_widget_initial_update"

        fun requestImmediateUpdate(context: Context) {
            val request = OneTimeWorkRequestBuilder<RatesWidgetUpdateWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                WIDGET_INITIAL_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }

        fun scheduleBackgroundUpdates(context: Context) {
            val request = PeriodicWorkRequestBuilder<RatesWidgetUpdateWorker>(
                15, TimeUnit.MINUTES
            ).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WIDGET_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}
