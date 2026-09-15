package com.arz.rates.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.clickable
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.defaultWeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.TextUnit
import androidx.glance.unit.dp
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.arz.rates.MainActivity
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

/*
 * ------------------------------------------------------------
 * ARZRATES WIDGET
 * ------------------------------------------------------------
 *
 * Responsive currency widget:
 *
 * 1 currency  -> large single row
 * 2 currencies -> large rows
 * 3 currencies -> large rows
 * 4+ currencies -> compact grid
 *
 * Automatically follows Android light/dark mode.
 *
 * Light:
 *   soft white background
 *   black text
 *
 * Dark:
 *   soft near-black background
 *   white text
 *
 * Data comes from the same selected currencies used by the app.
 */

// ------------------------------------------------------------
// COLORS
// ------------------------------------------------------------

private val DayBackground = Color(0xFFF7F8FA)
private val NightBackground = Color(0xFF202124)

private val DayText = Color(0xFF17181A)
private val NightText = Color(0xFFF7F7F7)

private val DayMuted = Color(0xFF69717A)
private val NightMuted = Color(0xFFB8BEC6)

private val DayUp = Color(0xFF16845B)
private val NightUp = Color(0xFF63D19A)

private val DayDown = Color(0xFFC33E43)
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

// ------------------------------------------------------------
// WIDGET
// ------------------------------------------------------------

class RatesWidget : GlanceAppWidget() {

    /*
     * Exact mode lets the widget react to the actual size chosen
     * by the launcher.
     */
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {

        /*
         * Load everything away from the UI thread.
         */
        val data = withContext(Dispatchers.IO) {

            val preferences = PreferencesRepository(context)

            val selectedKeys = preferences.selected.first()

            val language = preferences.language.first()

            /*
             * First try cached data.
             */
            var rates = WidgetCache.readRates(context)

            /*
             * If cache is empty, fetch fresh rates.
             */
            if (rates.isEmpty()) {
                rates = runCatching {
                    RatesRepository().fetch()
                }.getOrDefault(emptyList())

                if (rates.isNotEmpty()) {
                    WidgetCache.saveRates(
                        context,
                        rates
                    )
                }
            }

            /*
             * Preserve exactly the user's selected order.
             */
            val selectedRates = selectedKeys.mapNotNull { key ->
                rates.firstOrNull {
                    it.key.equals(
                        key,
                        ignoreCase = true
                    )
                }
            }

            WidgetData(
                language = language,
                selected = selectedRates
            )
        }

        provideContent {
            WidgetContent(data)
        }
    }
}

// ------------------------------------------------------------
// DATA
// ------------------------------------------------------------

private data class WidgetData(
    val language: Language,
    val selected: List<RateItem>
)

// ------------------------------------------------------------
// MAIN WIDGET CONTENT
// ------------------------------------------------------------

@Composable
private fun WidgetContent(
    data: WidgetData
) {

    val size = LocalSize.current

    /*
     * Determine layout from actual widget width.
     *
     * Small:
     *   1 column
     *
     * Medium:
     *   2 columns when many currencies
     *
     * Large:
     *   3 columns when many currencies
     */
    val columns = when {

        size.width < 220.dp -> 1

        size.width < 320.dp -> {
            if (data.selected.size <= 3) {
                1
            } else {
                2
            }
        }

        else -> {
            if (data.selected.size <= 3) {
                1
            } else {
                3
            }
        }
    }

    /*
     * More space = more currencies.
     */
    val visibleLimit = when (columns) {
        1 -> 5
        2 -> 8
        else -> 12
    }

    val visibleCurrencies =
        data.selected.take(visibleLimit)

    val remaining =
        (data.selected.size - visibleCurrencies.size)
            .coerceAtLeast(0)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(BackgroundProvider)
            .cornerRadius(24.dp)
            .padding(
                horizontal = 15.dp,
                vertical = 13.dp
            )
            .clickable(
                actionStartActivity<MainActivity>()
            ),

        verticalAlignment = Alignment.Top,

        horizontalAlignment = Alignment.Start
    ) {

        // ----------------------------------------------------
        // HEADER
        // ----------------------------------------------------

        Row(
            modifier = GlanceModifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    GlanceModifier.defaultWeight()
            ) {

                Text(
                    text =
                        if (
                            data.language ==
                            Language.PERSIAN
                        ) {
                            "نرخ ارز"
                        } else {
                            "LIVE RATES"
                        },

                    style = TextStyle(
                        color = TextProvider,
                        fontSize = widgetSp(14),
                        fontWeight =
                            FontWeight.Bold
                    )
                )

                Text(
                    text =
                        if (
                            data.language ==
                            Language.PERSIAN
                        ) {
                            "نرخ‌های انتخاب‌شده"
                        } else {
                            "Your selected currencies"
                        },

                    style = TextStyle(
                        color = MutedProvider,
                        fontSize = widgetSp(9)
                    )
                )
            }

            /*
             * Live indicator.
             */
            Text(
                text = "●",

                style = TextStyle(
                    color = UpProvider,
                    fontSize = widgetSp(12),
                    fontWeight =
                        FontWeight.Bold
                )
            )
        }

        Spacer(
            modifier =
                GlanceModifier.height(5.dp)
        )

        // ----------------------------------------------------
        // EMPTY STATE
        // ----------------------------------------------------

        if (visibleCurrencies.isEmpty()) {

            Text(
                text =
                    if (
                        data.language ==
                        Language.PERSIAN
                    ) {
                        "ارزها را در برنامه انتخاب کنید"
                    } else {
                        "Choose currencies in the app"
                    },

                style = TextStyle(
                    color = MutedProvider,
                    fontSize = widgetSp(11)
                )
            )

        } else {

            // ------------------------------------------------
            // CURRENCY GRID
            // ------------------------------------------------

            visibleCurrencies
                .chunked(columns)
                .forEach { rowItems ->

                    Row(
                        modifier =
                            GlanceModifier
                                .fillMaxWidth()
                                .padding(
                                    vertical =
                                        if (columns == 1) {
                                            4.dp
                                        } else {
                                            3.dp
                                        }
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        rowItems.forEach { item ->

                            WidgetRateCell(
                                item = item,
                                language = data.language,
                                columns = columns
                            )
                        }

                        /*
                         * Fill empty cells so the final row
                         * keeps the same alignment.
                         */
                        repeat(
                            columns - rowItems.size
                        ) {

                            Spacer(
                                modifier =
                                    GlanceModifier
                                        .defaultWeight()
                            )
                        }
                    }
                }

            // ------------------------------------------------
            // REMAINING COUNT
            // ------------------------------------------------

            if (remaining > 0) {

                Text(
                    text =
                        if (
                            data.language ==
                            Language.PERSIAN
                        ) {
                            "+$remaining ارز دیگر"
                        } else {
                            "+$remaining more"
                        },

                    style = TextStyle(
                        color = MutedProvider,
                        fontSize = widgetSp(9),
                        fontWeight =
                            FontWeight.Bold
                    ),

                    modifier =
                        GlanceModifier.padding(
                            top = 2.dp
                        )
                )
            }
        }
    }
}

// ------------------------------------------------------------
// CURRENCY CELL
// ------------------------------------------------------------

@Composable
private fun WidgetRateCell(
    item: RateItem,
    language: Language,
    columns: Int
) {

    val value =
        formatWidgetNumber(
            raw = item.rate.value ?: "—",
            language = language
        )

    val change =
        item.rate.change ?: 0.0

    val positive =
        change >= 0

    val flag =
        currencyFlag(item.key)

    Row(
        modifier =
            GlanceModifier.defaultWeight(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // ----------------------------------------------------
        // FLAG / ICON
        // ----------------------------------------------------

        Text(
            text = flag,

            style = TextStyle(
                fontSize =
                    if (columns == 1) {
                        widgetSp(16)
                    } else {
                        widgetSp(13)
                    }
            )
        )

        Spacer(
            modifier =
                GlanceModifier.width(5.dp)
        )

        // ----------------------------------------------------
        // CODE + NAME
        // ----------------------------------------------------

        Column(
            modifier =
                GlanceModifier.defaultWeight()
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        item.key.uppercase(),

                    style = TextStyle(
                        color = TextProvider,

                        fontSize =
                            if (columns == 1) {
                                widgetSp(12)
                            } else {
                                widgetSp(10)
                            },

                        fontWeight =
                            FontWeight.Bold
                    )
                )

                Spacer(
                    modifier =
                        GlanceModifier.width(3.dp)
                )

                Text(
                    text =
                        if (positive) {
                            "▲"
                        } else {
                            "▼"
                        },

                    style = TextStyle(
                        color =
                            if (positive) {
                                UpProvider
                            } else {
                                DownProvider
                            },

                        fontSize =
                            widgetSp(8)
                    )
                )
            }

            /*
             * Full currency name is shown only in
             * the large one-column layout.
             */
            if (columns == 1) {

                Text(
                    text =
                        CurrencyNames.nameFor(
                            item.key,
                            language
                        ),

                    style = TextStyle(
                        color = MutedProvider,
                        fontSize = widgetSp(8)
                    )
                )
            }
        }

        // ----------------------------------------------------
        // VALUE
        // ----------------------------------------------------

        Text(
            text = value,

            style = TextStyle(
                color = TextProvider,

                fontSize =
                    if (columns == 1) {
                        widgetSp(12)
                    } else {
                        widgetSp(10)
                    },

                fontWeight =
                    FontWeight.Bold
            )
        )
    }
}

// ------------------------------------------------------------
// GLANCE SP HELPER
// ------------------------------------------------------------

/*
 * IMPORTANT:
 *
 * Do NOT use:
 *
 *     12.Sp
 *
 * and do not rely on an unresolved `sp` extension.
 *
 * This helper creates the Glance TextUnit explicitly.
 */
private fun widgetSp(
    value: Int
): TextUnit {
    return TextUnit.Sp(value)
}

// ------------------------------------------------------------
// NUMBER FORMAT
// ------------------------------------------------------------

private fun formatWidgetNumber(
    raw: String,
    language: Language
): String {

    val number =
        raw
            .replace(",", "")
            .toDoubleOrNull()
            ?: return raw

    val locale =
        if (
            language == Language.PERSIAN
        ) {
            Locale.forLanguageTag("fa-IR")
        } else {
            Locale.US
        }

    return NumberFormat
        .getNumberInstance(locale)
        .format(number)
}

// ------------------------------------------------------------
// CURRENCY FLAGS
// ------------------------------------------------------------

private fun currencyFlag(
    key: String
): String {

    val code =
        key
            .lowercase()
            .substringBefore('_')

    val country =
        mapOf(

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

            // Crypto / commodities
            "btc" to "",
            "eth" to "",
            "xau" to "",
            "xag" to ""
        )

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

    val countryCode =
        country[code]

    if (countryCode == null) {
        return "💱"
    }

    return countryCode
        .map { character ->

            Character.codePointAt(
                character.toString(),
                0
            ) + 127397
        }
        .joinToString("") { codePoint ->

            String(
                Character.toChars(codePoint)
            )
        }
}

// ------------------------------------------------------------
// WIDGET RECEIVER
// ------------------------------------------------------------

class RatesWidgetReceiver :
    GlanceAppWidgetReceiver() {

    override val glanceAppWidget:
        GlanceAppWidget = RatesWidget()

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
                OneTimeWorkRequestBuilder<
                    RatesWidgetUpdateWorker
                    >()
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
                PeriodicWorkRequestBuilder<
                    RatesWidgetUpdateWorker
                    >(
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
