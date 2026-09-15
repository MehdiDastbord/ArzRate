package com.arz.rates.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.arz.rates.R
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arz.rates.data.CurrencyNames
import com.arz.rates.data.Language
import com.arz.rates.data.RateItem
import com.arz.rates.data.ThemeMode
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

private data class Strings(
    val home: String, val explore: String, val calculator: String, val settings: String,
    val yourRates: String, val liveDashboard: String, val updating: String, val retry: String,
    val buildDashboard: String, val chooseRates: String, val searchCurrencies: String,
    val searchNameCode: String, val continueWith: String, val ratesAvailable: String,
    val addFromExplore: String, val remove: String, val add: String, val updatedRecently: String,
    val automaticUpdates: String, val automaticUpdatesDescription: String, val selectedRates: String,
    val selected: String, val language: String, val english: String, val persian: String,
    val noRates: String, val appearance: String, val system: String, val light: String, val dark: String,
    val holdToReorder: String, val calculatorTitle: String, val amount: String, val from: String,
    val to: String, val result: String, val swap: String, val chooseCurrency: String,
    val about: String, val developer: String, val widgetHint: String
)

private fun strings(language: Language) = if (language == Language.PERSIAN) {
    Strings(
        home = "خانه", explore = "ارزها", calculator = "ماشین حساب", settings = "تنظیمات",
        yourRates = "نرخ‌های شما", liveDashboard = "نرخ لحظه‌ای بازار", updating = "در حال بروزرسانی…",
        retry = "تلاش دوباره", buildDashboard = "داشبورد خودت را بساز",
        chooseRates = "ارزهایی را که می‌خواهی در خانه ببینی انتخاب کن.", searchCurrencies = "جستجوی ارزها…",
        searchNameCode = "جستجو بر اساس نام یا کد…", continueWith = "ادامه با %d انتخاب",
        ratesAvailable = "%d نرخ موجود است", addFromExplore = "از بخش ارزها ارز اضافه کن.", remove = "حذف",
        add = "افزودن", updatedRecently = "به‌تازگی بروزرسانی شد", automaticUpdates = "بروزرسانی خودکار",
        automaticUpdatesDescription = "نرخ‌ها هنگام باز بودن برنامه مرتب بروزرسانی می‌شوند و ویجت نیز در پس‌زمینه تازه می‌شود.",
        selectedRates = "ارزهای انتخاب‌شده", selected = "%d انتخاب شده", language = "زبان", english = "English",
        persian = "فارسی", noRates = "هنوز نرخی دریافت نشده است.", appearance = "ظاهر برنامه",
        system = "سیستم", light = "روشن", dark = "تیره", holdToReorder = "برای جابه‌جایی، روی ارز نگه دار و بکش.",
        calculatorTitle = "تبدیل سریع ارز", amount = "مقدار", from = "از", to = "به", result = "نتیجه",
        swap = "جابه‌جایی", chooseCurrency = "انتخاب ارز", about = "درباره برنامه", developer = "سازنده: Mehdi Dastbord",
        widgetHint = "ویجت با اندازه صفحه سازگار می‌شود و با حالت روشن/تیره گوشی هماهنگ است."
    )
} else {
    Strings(
        home = "Home", explore = "Currencies", calculator = "Calculator", settings = "Settings",
        yourRates = "Your Rates", liveDashboard = "Live market dashboard", updating = "Updating…",
        retry = "Retry", buildDashboard = "Build your dashboard", chooseRates = "Choose the currencies you want on Home.",
        searchCurrencies = "Search currencies…", searchNameCode = "Search by name or code…",
        continueWith = "Continue with %d selected", ratesAvailable = "%d rates available",
        addFromExplore = "Add currencies from Currencies.", remove = "Remove", add = "Add",
        updatedRecently = "Updated recently", automaticUpdates = "Automatic updates",
        automaticUpdatesDescription = "Rates refresh while the app is open and the widget also refreshes in the background.",
        selectedRates = "Selected currencies", selected = "%d selected", language = "Language", english = "English",
        persian = "فارسی", noRates = "No rates have been received yet.", appearance = "Appearance",
        system = "System", light = "Light", dark = "Dark", holdToReorder = "Hold a currency and drag it to reorder.",
        calculatorTitle = "Currency converter", amount = "Amount", from = "From", to = "To", result = "Result",
        swap = "Swap", chooseCurrency = "Choose currency", about = "About", developer = "Developer: Mehdi Dastbord",
        widgetHint = "The widget adapts to its size and follows the phone's light/dark mode."
    )
}

@Composable
fun ArzApp(vm: AppViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val text = remember(state.language) { strings(state.language) }
    val direction = if (state.language == Language.PERSIAN) LayoutDirection.Rtl else LayoutDirection.Ltr
    var tab by rememberSaveable { mutableIntStateOf(0) }

    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(tab == 0, { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text(text.home) })
                    NavigationBarItem(tab == 1, { tab = 1 }, icon = { Icon(Icons.Default.Explore, null) }, label = { Text(text.explore) })
                    NavigationBarItem(tab == 2, { tab = 2 }, icon = { Icon(Icons.Default.Calculate, null) }, label = { Text(text.calculator) })
                    NavigationBarItem(tab == 3, { tab = 3 }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text(text.settings) })
                }
            }
        ) { padding ->
            AnimatedContent<Int>(
                targetState = tab,
                modifier = Modifier.padding(padding).fillMaxSize(),
                transitionSpec = {
                    (slideInHorizontally { it / 8 } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it / 8 } + fadeOut())
                },
                label = "tab_transition"
            ) { selectedTab ->
                when (selectedTab) {
                    0 -> HomeScreen(state, vm, text)
                    1 -> ExploreScreen(state, vm, text)
                    2 -> CalculatorScreen(state, text)
                    else -> SettingsScreen(state, vm, text)
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(state: AppState, vm: AppViewModel, text: Strings) {
    val selectedItems = state.selected.mapNotNull { key -> state.rates.find { it.key.equals(key, true) } }
    val listState = rememberLazyListState()
    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(text.yourRates, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(if (state.loading) text.updating else text.liveDashboard, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            FilledTonalIconButton(onClick = vm::refresh) { Icon(Icons.Default.Refresh, null) }
        }
        AnimatedVisibility(state.error != null) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(state.error ?: "", Modifier.weight(1f))
                    TextButton(onClick = vm::refresh) { Text(text.retry) }
                }
            }
        }
        if (selectedItems.isEmpty()) {
            WelcomeSelector(state, vm, text)
        } else {
            Text(text.holdToReorder, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 12.dp))
            LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 14.dp)) {
                itemsIndexed(selectedItems, key = { _, item -> item.key }) { index, item ->
                    ReorderableRateCard(item, state, vm, text, listState)
                }
            }
        }
    }
}

@Composable
private fun ReorderableRateCard(item: RateItem, state: AppState, vm: AppViewModel, text: Strings, listState: androidx.compose.foundation.lazy.LazyListState) {
    var dragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val isRtl = state.language == Language.PERSIAN
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .graphicsLayer {
                translationY = if (dragging) dragOffset else 0f
                shadowElevation = if (dragging) 18f else 0f
            }
            .pointerInput(item.key) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { dragging = true; dragOffset = 0f },
                    onDragCancel = { dragging = false; dragOffset = 0f },
                    onDragEnd = { dragging = false; dragOffset = 0f },
                    onDrag = { change, amount ->
                        dragOffset += amount.y
                        val currentIndex = state.selected.indexOf(item.key)
                        val current = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == item.key } ?: return@detectDragGesturesAfterLongPress
                        val center = current.offset + current.size / 2f + dragOffset
                        val target = listState.layoutInfo.visibleItemsInfo
                            .minByOrNull { abs((it.offset + it.size / 2f) - center) }
                            ?.index ?: currentIndex
                        if (target != currentIndex && target in state.selected.indices && currentIndex >= 0) {
                            vm.reorder(currentIndex, target)
                            dragOffset = 0f
                        }
                    }
                )
            },
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(currencyFlag(item.key), style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(item.displayName(state.language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(item.key.uppercase(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(formatNumber(item.rate.value ?: "—", state.language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val change = item.rate.change ?: 0.0
                Text(if (change >= 0) "▲ ${formatDecimal(change, state.language)}" else "▼ ${formatDecimal(abs(change), state.language)}", color = if (change >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Text(item.rate.date ?: text.updatedRecently, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun WelcomeSelector(state: AppState, vm: AppViewModel, text: Strings) {
    var query by remember { mutableStateOf("") }
    val filtered = state.rates.filter { it.displayName(state.language).contains(query, true) || it.key.contains(query, true) }
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(22.dp))
        Text(text.buildDashboard, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text.chooseRates, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp, bottom = 16.dp))
        OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), placeholder = { Text(text.searchCurrencies) }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(filtered, key = { it.key }) { item -> SelectRow(item, item.key in state.selected, state.language, text) { vm.toggle(item.key) } }
        }
        if (state.selected.isNotEmpty()) {
            Text(
                text.continueWith.format(state.selected.size),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun ExploreScreen(state: AppState, vm: AppViewModel, text: Strings) {
    var query by remember { mutableStateOf("") }
    val filtered = state.rates.filter { it.displayName(state.language).contains(query, true) || it.key.contains(query, true) }
    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(16.dp))
        Text(text.explore, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(text.ratesAvailable.format(state.rates.size), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), placeholder = { Text(text.searchNameCode) }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
            items(filtered, key = { it.key }) { item -> SelectRow(item, item.key in state.selected, state.language, text) { vm.toggle(item.key) } }
        }
    }
}

@Composable
private fun SelectRow(item: RateItem, selected: Boolean, language: Language, text: Strings, onToggle: () -> Unit) {
    ListItem(
        headlineContent = { Row(verticalAlignment = Alignment.CenterVertically) { Text(currencyFlag(item.key), style = MaterialTheme.typography.titleLarge); Spacer(Modifier.width(10.dp)); Text(item.displayName(language), fontWeight = FontWeight.SemiBold) } },
        supportingContent = { Text(item.key.uppercase()) },
        trailingContent = { FilledTonalButton(onClick = onToggle) { Icon(if (selected) Icons.Default.SwapVert else Icons.Default.Add, null); Spacer(Modifier.width(4.dp)); Text(if (selected) text.remove else text.add) } },
        modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle)
    )
}

@Composable
private fun CalculatorScreen(state: AppState, text: Strings) {
    val available = state.rates
    var amountText by remember { mutableStateOf("1") }
    var from by remember(state.selected, available) { mutableStateOf(state.selected.firstOrNull() ?: available.firstOrNull()?.key ?: "usd") }
    var to by remember(state.selected, available) { mutableStateOf(state.selected.getOrNull(1) ?: available.getOrNull(1)?.key ?: "aed") }
    var showFrom by remember { mutableStateOf(false) }
    var showTo by remember { mutableStateOf(false) }
    val fromRate = available.firstOrNull { it.key.equals(from, true) }?.rate?.numericValue()
    val toRate = available.firstOrNull { it.key.equals(to, true) }?.rate?.numericValue()
    val amount = amountText.replace(",", "").toDoubleOrNull() ?: 0.0
    val result = if (fromRate != null && toRate != null && toRate != 0.0) amount * fromRate / toRate else 0.0

    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Spacer(Modifier.height(12.dp))
        Text(text.calculatorTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(amountText, { amountText = it }, Modifier.fillMaxWidth(), label = { Text(text.amount) }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        CurrencyPicker(text.from, from, available, state.language, { showFrom = true })
        IconButton(onClick = { val temp = from; from = to; to = temp }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Icon(Icons.Default.SwapVert, text.swap) }
        CurrencyPicker(text.to, to, available, state.language, { showTo = true })
        Spacer(Modifier.height(18.dp))
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(20.dp)) {
                Text(text.result, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${currencyFlag(to)}  ${formatNumber(result.toString(), state.language)} ${to.uppercase()}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showFrom) CurrencyChooserDialog(text.chooseCurrency, available, state.language, { from = it; showFrom = false }, { showFrom = false })
    if (showTo) CurrencyChooserDialog(text.chooseCurrency, available, state.language, { to = it; showTo = false }, { showTo = false })
}

@Composable
private fun CurrencyPicker(label: String, key: String, rates: List<RateItem>, language: Language, onClick: () -> Unit) {
    val item = rates.firstOrNull { it.key.equals(key, true) }
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
        Text(currencyFlag(key), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(item?.displayName(language) ?: key.uppercase(), fontWeight = FontWeight.Bold)
        }
        Text(key.uppercase())
    }
}

@Composable
private fun CurrencyChooserDialog(title: String, rates: List<RateItem>, language: Language, onPick: (String) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(title) }, text = {
        LazyColumn(Modifier.heightIn(max = 420.dp)) {
            items(rates, key = { it.key }) { item ->
                TextButton(onClick = { onPick(item.key) }, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(currencyFlag(item.key), style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.width(10.dp))
                        Text(item.displayName(language), Modifier.weight(1f))
                        Text(item.key.uppercase())
                    }
                }
            }
        }
    }, confirmButton = {})
}

@Composable
private fun SettingsScreen(state: AppState, vm: AppViewModel, text: Strings) {
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Spacer(Modifier.height(12.dp))
        Text(text.settings, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text(text.language, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(state.language == Language.ENGLISH, { vm.setLanguage(Language.ENGLISH) }, label = { Text(text.english) })
                    FilterChip(state.language == Language.PERSIAN, { vm.setLanguage(Language.PERSIAN) }, label = { Text(text.persian) })
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text(text.appearance, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(state.theme == ThemeMode.SYSTEM, { vm.setTheme(ThemeMode.SYSTEM) }, label = { Icon(Icons.Default.Settings, null); Spacer(Modifier.width(4.dp)); Text(text.system) })
                    FilterChip(state.theme == ThemeMode.LIGHT, { vm.setTheme(ThemeMode.LIGHT) }, label = { Icon(Icons.Default.LightMode, null); Spacer(Modifier.width(4.dp)); Text(text.light) })
                    FilterChip(state.theme == ThemeMode.DARK, { vm.setTheme(ThemeMode.DARK) }, label = { Icon(Icons.Default.DarkMode, null); Spacer(Modifier.width(4.dp)); Text(text.dark) })
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text(text.selectedRates, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text.selected.format(state.selected.size), color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                Text(text.holdToReorder, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text(text.automaticUpdates, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text.automaticUpdatesDescription, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
                Text(text.widgetHint, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
            }
        }
        Spacer(Modifier.weight(1f))
        Image(
            painter = painterResource(R.drawable.app_logo),
            contentDescription = text.about,
            modifier = Modifier.size(54.dp).clip(RoundedCornerShape(16.dp)).align(Alignment.CenterHorizontally)
        )
        Text(text.about, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp))
        Text(text.developer, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp, bottom = 8.dp))
    }
}

private fun RateItem.displayName(language: Language): String = CurrencyNames.nameFor(key, language)

private fun formatNumber(raw: String, language: Language): String {
    val n = raw.replace(",", "").toDoubleOrNull() ?: return raw
    val locale = if (language == Language.PERSIAN) Locale.forLanguageTag("fa-IR") else Locale.US
    return NumberFormat.getNumberInstance(locale).format(n)
}

private fun formatDecimal(n: Double, language: Language): String {
    val locale = if (language == Language.PERSIAN) Locale.forLanguageTag("fa-IR") else Locale.US
    return NumberFormat.getNumberInstance(locale).format(n)
}

private fun currencyFlag(key: String): String {
    val code = key.lowercase().substringBefore('_')
    val country = mapOf(
        "afn" to "AF", "all" to "AL", "amd" to "AM", "ang" to "CW", "aoa" to "AO", "ars" to "AR", "awg" to "AW", "azn" to "AZ",
        "bam" to "BA", "bbd" to "BB", "bdt" to "BD", "bgn" to "BG", "bhd" to "BH", "bif" to "BI", "bmd" to "BM", "bnd" to "BN",
        "bob" to "BO", "brl" to "BR", "bsd" to "BS", "btn" to "BT", "bwp" to "BW", "byn" to "BY", "bzd" to "BZ",
        "cad" to "CA", "cdf" to "CD", "chf" to "CH", "clp" to "CL", "cny" to "CN", "cop" to "CO", "crc" to "CR", "cuc" to "CU",
        "cup" to "CU", "cve" to "CV", "czk" to "CZ", "djf" to "DJ", "dkk" to "DK", "dop" to "DO", "dzd" to "DZ", "egp" to "EG",
        "ern" to "ER", "etb" to "ET", "eur" to "EU", "fjd" to "FJ", "fkp" to "FK", "gbp" to "GB", "gel" to "GE", "ghs" to "GH",
        "gmd" to "GM", "gnf" to "GN", "gtq" to "GT", "gyd" to "GY", "hkd" to "HK", "hnl" to "HN", "hrk" to "HR", "htg" to "HT",
        "huf" to "HU", "idr" to "ID", "ils" to "IL", "imp" to "IM", "inr" to "IN", "iqd" to "IQ", "isk" to "IS", "jmd" to "JM",
        "jod" to "JO", "jpy" to "JP", "kes" to "KE", "kgs" to "KG", "khr" to "KH", "kmf" to "KM", "kpw" to "KP", "krw" to "KR",
        "kzt" to "KZ", "lak" to "LA", "lbp" to "LB", "lkr" to "LK", "lrd" to "LR", "lsl" to "LS", "lyd" to "LY", "mad" to "MA",
        "mdl" to "MD", "mga" to "MG", "mkd" to "MK", "mmk" to "MM", "mnt" to "MN", "mop" to "MO", "mru" to "MR", "mur" to "MU",
        "mvr" to "MV", "mwk" to "MW", "mxn" to "MX", "myr" to "MY", "mzn" to "MZ", "nad" to "NA", "ngn" to "NG", "nio" to "NI",
        "nok" to "NO", "npr" to "NP", "nzd" to "NZ", "omr" to "OM", "pab" to "PA", "pen" to "PE", "pgk" to "PG", "php" to "PH",
        "pkr" to "PK", "pln" to "PL", "pyg" to "PY", "qar" to "QA", "ron" to "RO", "rsd" to "RS", "rub" to "RU", "rwf" to "RW",
        "sar" to "SA", "sbd" to "SB", "scr" to "SC", "sdg" to "SD", "sek" to "SE", "sgd" to "SG", "shp" to "SH", "sle" to "SL",
        "sll" to "SL", "sos" to "SO", "srd" to "SR", "ssp" to "SS", "stn" to "ST", "svc" to "SV", "syp" to "SY", "szl" to "SZ",
        "thb" to "TH", "tjs" to "TJ", "tmt" to "TM", "tnd" to "TN", "top" to "TO", "try" to "TR", "ttd" to "TT", "twd" to "TW",
        "tzs" to "TZ", "uah" to "UA", "ugx" to "UG", "usd" to "US", "uyu" to "UY", "uzs" to "UZ", "ves" to "VE", "vnd" to "VN",
        "vuv" to "VU", "wst" to "WS", "xaf" to "CM", "xcd" to "AG", "xof" to "SN", "xpf" to "PF", "yer" to "YE", "zar" to "ZA",
        "zmw" to "ZM", "zwl" to "ZW", "aed" to "AE", "aud" to "AU", "btc" to "", "eth" to "", "xau" to "", "xag" to ""
    )
    val code2 = country[code]
    if (code == "btc") return "₿"
    if (code == "eth") return "Ξ"
    if (code == "xau") return "🥇"
    if (code == "xag") return "🥈"
    if (code2 == null) return "💱"
    return code2.map { Character.codePointAt(it.toString(), 0) + 127397 }.joinToString("") { String(Character.toChars(it)) }
}
