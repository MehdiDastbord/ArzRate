package com.arz.rates.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.dp
import androidx.glance.unit.sp
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
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

private val DayBackground = Color(0xFFF8F9FA)
private val NightBackground = Color(0xFF202124)

private val DayText = Color(0xFF17181A)
private val NightText = Color(0xFFF7F7F7)

private val DayMuted = Color(0xFF687078)
private val NightMuted = Color(0xFFB9BEC5)

private val DayUp = Color(0xFF137A55)
private val NightUp = Color(0xFF63D19A)

private val DayDown = Color(0xFFC0393B)
private val NightDown = Color(0xFFFF8585)

private val BackgroundProvider = ColorProvider(
    day = DayBackground,
    night = NightBackground
)

private val TextProvider = ColorProvider(
    day = DayText,
    night = NightText
)

private val MutedProvider = ColorProvider(
    day = DayMuted,
    night = NightMuted
)

private val UpProvider = ColorProvider(
    day = DayUp,
    night = NightUp
)

private val DownProvider = ColorProvider(
    day = DayDown,
    night = NightDown
)

class RatesWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val data = withContext(Dispatchers.IO) {

            val prefs = PreferencesRepository(context)

            val selected = prefs.selected.first()

            val language = prefs.language.first()

            var rates = WidgetCache.readRates(context)

            /*
             * If there is no cached data yet, fetch it immediately.
             */
            if (rates.isEmpty()) {
                rates = runCatching {
                    RatesRepository().fetch()
                }.getOrDefault(emptyList())

                if (rates.isNotEmpty()) {
                    WidgetCache.saveRates(context, rates)
                }
            }

            WidgetData(
                language = language,
                selected = selected.mapNotNull { key ->
                    rates.find {
                        it.key.equals(
                            key,
                            ignoreCase = true
                        )
                    }
                }
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
private fun WidgetContent(
    data: WidgetData
) {
    // Keep the widget deliberately simple and stable. Android launchers can
    // resize widgets in many ways, so the layout uses one vertical list and
    // avoids Glance's weight/sizing APIs that caused build and layout issues.
    val visible = data.selected.take(8)
    val remaining = (data.selected.size - visible.size).coerceAtLeast(0)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(BackgroundProvider)
            .cornerRadius(24.dp)
            .padding(
                horizontal = 16.dp,
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = if (data.language == Language.PERSIAN) {
                "نرخ ارز"
            } else {
                "LIVE RATES"
            },
            style = TextStyle(
                color = TextProvider,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            text = if (data.language == Language.PERSIAN) {
                "نرخ‌های انتخاب‌شده"
            } else {
                "Selected currencies"
            },
            style = TextStyle(
                color = MutedProvider,
                fontSize = 9.sp
            )
        )

        Spacer(modifier = GlanceModifier.height(8.dp))

        if (visible.isEmpty()) {
            Text(
                text = if (data.language == Language.PERSIAN) {
                    "ارزها را در برنامه انتخاب کنید"
                } else {
                    "Choose currencies in the app"
                },
                style = TextStyle(
                    color = MutedProvider,
                    fontSize = 11.sp
                )
            )
        } else {
            visible.forEach { item ->
                WidgetRateCell(
                    item = item,
                    language = data.language
                )
            }

            if (remaining > 0) {
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = if (data.language == Language.PERSIAN) {
                        "+$remaining ارز دیگر"
                    } else {
                        "+$remaining more"
                    },
                    style = TextStyle(
                        color = MutedProvider,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun WidgetRateCell(
    item: RateItem,
    language: Language
) {
    val value = formatWidgetNumber(
        raw = item.rate.value ?: "—",
        language = language
    )
    val change = item.rate.change ?: 0.0
    val positive = change >= 0
    val flag = currencyFlag(item.key)

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = flag,
                style = TextStyle(
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = GlanceModifier.width(6.dp))

            Text(
                text = item.key.uppercase(),
                style = TextStyle(
                    color = TextProvider,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = GlanceModifier.width(6.dp))

            Text(
                text = if (positive) "▲" else "▼",
                style = TextStyle(
                    color = if (positive) UpProvider else DownProvider,
                    fontSize = 8.sp
                )
            )

            Spacer(modifier = GlanceModifier.width(6.dp))

            Text(
                text = value,
                style = TextStyle(
                    color = TextProvider,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Text(
            text = CurrencyNames.nameFor(item.key, language),
            style = TextStyle(
                color = MutedProvider,
                fontSize = 8.sp
            ),
            modifier = GlanceModifier.padding(start = 22.dp)
        )
    }
}

private fun formatWidgetNumber(
    raw: String,
    language: Language
): String {

    val n = raw
        .replace(",", "")
        .toDoubleOrNull()
        ?: return raw

    val locale =
        if (language == Language.PERSIAN) {
            Locale.forLanguageTag("fa-IR")
        } else {
            Locale.US
        }

    return NumberFormat
        .getNumberInstance(locale)
        .format(n)
}

private fun currencyFlag(
    key: String
): String {

    val code = key
        .lowercase()
        .substringBefore('_')

    val country = mapOf(

        "afn" to "AF",
        "all" to "AL",
        "amd" to "AM",
        "ang" to "CW",
        "aoa" to "AO",
        "ars" to "AR",
        "awg" to "AW",
        "azn" to "AZ",

        "bam" to "BA",
        "bbd" to "BB",
        "bdt" to "BD",
        "bgn" to "BG",
        "bhd" to "BH",
        "bif" to "BI",
        "bmd" to "BM",
        "bnd" to "BN",

        "bob" to "BO",
        "brl" to "BR",
        "bsd" to "BS",
        "btn" to "BT",
        "bwp" to "BW",
        "byn" to "BY",
        "bzd" to "BZ",

        "cad" to "CA",
        "cdf" to "CD",
        "chf" to "CH",
        "clp" to "CL",
        "cny" to "CN",
        "cop" to "CO",
        "crc" to "CR",
        "cuc" to "CU",
        "cup" to "CU",
        "cve" to "CV",
        "czk" to "CZ",

        "djf" to "DJ",
        "dkk" to "DK",
        "dop" to "DO",
        "dzd" to "DZ",

        "egp" to "EG",
        "ern" to "ER",
        "etb" to "ET",
        "eur" to "EU",

        "fjd" to "FJ",
        "fkp" to "FK",

        "gbp" to "GB",
        "gel" to "GE",
        "ghs" to "GH",
        "gmd" to "GM",
        "gnf" to "GN",
        "gtq" to "GT",
        "gyd" to "GY",

        "hkd" to "HK",
        "hnl" to "HN",
        "hrk" to "HR",
        "htg" to "HT",
        "huf" to "HU",

        "idr" to "ID",
        "ils" to "IL",
        "imp" to "IM",
        "inr" to "IN",
        "iqd" to "IQ",
        "isk" to "IS",

        "jmd" to "JM",
        "jod" to "JO",
        "jpy" to "JP",

        "kes" to "KE",
        "kgs" to "KG",
        "khr" to "KH",
        "kmf" to "KM",
        "kpw" to "KP",
        "krw" to "KR",
        "kzt" to "KZ",

        "lak" to "LA",
        "lbp" to "LB",
        "lkr" to "LK",
        "lrd" to "LR",
        "lsl" to "LS",
        "lyd" to "LY",

        "mad" to "MA",
        "mdl" to "MD",
        "mga" to "MG",
        "mkd" to "MK",
        "mmk" to "MM",
        "mnt" to "MN",
        "mop" to "MO",
        "mru" to "MR",
        "mur" to "MU",
        "mvr" to "MV",
        "mwk" to "MW",
        "mxn" to "MX",
        "myr" to "MY",
        "mzn" to "MZ",

        "nad" to "NA",
        "ngn" to "NG",
        "nio" to "NI",
        "nok" to "NO",
        "npr" to "NP",
        "nzd" to "NZ",

        "omr" to "OM",

        "pab" to "PA",
        "pen" to "PE",
        "pgk" to "PG",
        "php" to "PH",
        "pkr" to "PK",
        "pln" to "PL",
        "pyg" to "PY",

        "qar" to "QA",

        "ron" to "RO",
        "rsd" to "RS",
        "rub" to "RU",
        "rwf" to "RW",

        "sar" to "SA",
        "sbd" to "SB",
        "scr" to "SC",
        "sdg" to "SD",
        "sek" to "SE",
        "sgd" to "SG",
        "shp" to "SH",
        "sle" to "SL",
        "sll" to "SL",
        "sos" to "SO",
        "srd" to "SR",
        "ssp" to "SS",
        "stn" to "ST",
        "svc" to "SV",
        "syp" to "SY",
        "szl" to "SZ",

        "thb" to "TH",
        "tjs" to "TJ",
        "tmt" to "TM",
        "tnd" to "TN",
        "top" to "TO",
        "try" to "TR",
        "ttd" to "TT",
        "twd" to "TW",
        "tzs" to "TZ",

        "uah" to "UA",
        "ugx" to "UG",
        "usd" to "US",
        "uyu" to "UY",
        "uzs" to "UZ",

        "ves" to "VE",
        "vnd" to "VN",
        "vuv" to "VU",

        "wst" to "WS",

        "xaf" to "CM",
        "xcd" to "AG",
        "xof" to "SN",
        "xpf" to "PF",

        "yer" to "YE",
        "zar" to "ZA",
        "zmw" to "ZM",
        "zwl" to "ZW",

        "aed" to "AE",
        "aud" to "AU",

        /*
         * Crypto / precious metals don't have country flags.
         */
        "btc" to "",
        "eth" to "",
        "xau" to "",
        "xag" to ""
    )

    val code2 = country[code]

    if (code == "btc") {
        return "₿"
    }

    if (code == "eth") {
        return "Ξ"
    }

    if (code == "xau") {
        return "🥇"
    }

    if (code == "xag") {
        return "🥈"
    }

    if (code2 == null) {
        return "💱"
    }

    return code2
        .map {
            Character.codePointAt(
                it.toString(),
                0
            ) + 127397
        }
        .joinToString("") {
            String(
                Character.toChars(it)
            )
        }
}

class RatesWidgetReceiver :
    GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget =
        RatesWidget()

    override fun onEnabled(
        context: Context
    ) {
        super.onEnabled(context)

        requestImmediateUpdate(context)

        scheduleBackgroundUpdates(context)
    }

    override fun onDisabled(
        context: Context
    ) {

        WorkManager
            .getInstance(context)
            .cancelUniqueWork(
                WIDGET_WORK_NAME
            )

        WorkManager
            .getInstance(context)
            .cancelUniqueWork(
                WIDGET_INITIAL_WORK_NAME
            )

        super.onDisabled(context)
    }

    companion object {

        const val WIDGET_WORK_NAME =
            "arz_rates_widget_updates"

        const val WIDGET_INITIAL_WORK_NAME =
            "arz_rates_widget_initial_update"

        fun requestImmediateUpdate(
            context: Context
        ) {

            val request =
                OneTimeWorkRequestBuilder<RatesWidgetUpdateWorker>()
                    .build()

            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(
                    WIDGET_INITIAL_WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    request
                )
        }

        fun scheduleBackgroundUpdates(
            context: Context
        ) {

            val request =
                PeriodicWorkRequestBuilder<RatesWidgetUpdateWorker>(
                    15,
                    TimeUnit.MINUTES
                ).build()

            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(
                    WIDGET_WORK_NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    request
                )
        }
    }
}