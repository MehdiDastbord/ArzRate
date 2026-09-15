package com.arz.rates.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arz.rates.data.CurrencyNames
import com.arz.rates.data.Language
import com.arz.rates.data.RateItem
import java.text.NumberFormat
import java.util.Locale

private data class Strings(
    val home: String,
    val explore: String,
    val settings: String,
    val yourRates: String,
    val liveDashboard: String,
    val updating: String,
    val retry: String,
    val buildDashboard: String,
    val chooseRates: String,
    val searchCurrencies: String,
    val searchNameCode: String,
    val continueWith: String,
    val ratesAvailable: String,
    val addFromExplore: String,
    val remove: String,
    val add: String,
    val updatedRecently: String,
    val automaticUpdates: String,
    val automaticUpdatesDescription: String,
    val selectedRates: String,
    val selected: String,
    val language: String,
    val english: String,
    val persian: String,
    val noRates: String
)

private fun strings(language: Language) = if (language == Language.PERSIAN) {
    Strings(
        home = "خانه", explore = "کاوش", settings = "تنظیمات",
        yourRates = "نرخ‌های شما", liveDashboard = "نرخ‌های لحظه‌ای بازار", updating = "در حال بروزرسانی…",
        retry = "تلاش دوباره", buildDashboard = "داشبورد خودت را بساز",
        chooseRates = "نرخ‌هایی را که می‌خواهی در صفحه خانه ببینی انتخاب کن. هر زمان خواستی می‌توانی تغییرشان بدهی.",
        searchCurrencies = "جستجوی ارزها…", searchNameCode = "جستجو بر اساس نام یا کد…",
        continueWith = "ادامه با %d انتخاب", ratesAvailable = "%d نرخ موجود است",
        addFromExplore = "از بخش کاوش ارز اضافه کن.", remove = "حذف", add = "افزودن",
        updatedRecently = "به‌تازگی بروزرسانی شد", automaticUpdates = "بروزرسانی خودکار",
        automaticUpdatesDescription = "نرخ‌ها هنگام باز بودن برنامه هر ۶۰ ثانیه بروزرسانی می‌شوند. ویجت نیز از بروزرسانی پس‌زمینه پشتیبانی می‌کند.",
        selectedRates = "نرخ‌های انتخاب‌شده", selected = "%d انتخاب شده",
        language = "زبان", english = "English", persian = "فارسی",
        noRates = "هنوز نرخی دریافت نشده است."
    )
} else {
    Strings(
        home = "Home", explore = "Explore", settings = "Settings",
        yourRates = "Your Rates", liveDashboard = "Live market dashboard", updating = "Updating…",
        retry = "Retry", buildDashboard = "Build your dashboard",
        chooseRates = "Choose the rates you want to see on Home. You can change them anytime.",
        searchCurrencies = "Search currencies…", searchNameCode = "Search by name or code…",
        continueWith = "Continue with %d selected", ratesAvailable = "%d rates available",
        addFromExplore = "Add currencies from Explore.", remove = "Remove", add = "Add",
        updatedRecently = "Updated recently", automaticUpdates = "Automatic updates",
        automaticUpdatesDescription = "Rates refresh every 60 seconds while the app is open. The widget also supports background updates.",
        selectedRates = "Selected rates", selected = "%d selected",
        language = "Language", english = "English", persian = "فارسی",
        noRates = "No rates have been received yet."
    )
}

@Composable
fun ArzApp(vm: AppViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val text = remember(state.language) { strings(state.language) }
    val direction = if (state.language == Language.PERSIAN) LayoutDirection.Rtl else LayoutDirection.Ltr
    var tab by remember { mutableIntStateOf(0) }

    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text(text.home) })
                    NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.Explore, null) }, label = { Text(text.explore) })
                    NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text(text.settings) })
                }
            }
        ) { padding ->
            when (tab) {
                0 -> HomeScreen(state, vm, text, Modifier.padding(padding))
                1 -> ExploreScreen(state, vm, text, Modifier.padding(padding))
                else -> SettingsScreen(state, vm, text, Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun HomeScreen(state: AppState, vm: AppViewModel, text: Strings, modifier: Modifier) {
    var firstRun by remember(state.selected) { mutableStateOf(state.selected.isEmpty()) }
    Column(modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(text.yourRates, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(if (state.loading) text.updating else text.liveDashboard, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = vm::refresh) { Icon(Icons.Default.Refresh, null) }
        }
        AnimatedVisibility(state.error != null) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(state.error ?: "", Modifier.weight(1f))
                    TextButton(onClick = vm::refresh) { Text(text.retry) }
                }
            }
        }
        if (firstRun) {
            WelcomeSelector(state, vm, text) { firstRun = false }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 18.dp)) {
                items(state.selected.mapNotNull { key -> state.rates.find { it.key == key } }, key = { it.key }) { item ->
                    RateCard(item, state.language, text)
                }
                item {
                    if (state.selected.isEmpty()) Text(text.addFromExplore, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    else if (state.rates.isEmpty()) Text(text.noRates, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun WelcomeSelector(state: AppState, vm: AppViewModel, text: Strings, done: () -> Unit) {
    var query by remember { mutableStateOf("") }
    val filtered = state.rates.filter { item ->
        item.displayName(state.language).contains(query, true) || item.key.contains(query, true)
    }
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(22.dp))
        Text(text.buildDashboard, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text.chooseRates, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp, bottom = 16.dp))
        OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text(text.searchCurrencies) }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(filtered, key = { it.key }) { item -> SelectRow(item, item.key in state.selected, state.language, text) { vm.toggle(item.key) } }
        }
        Button(onClick = done, enabled = state.selected.isNotEmpty(), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Text(text.continueWith.format(state.selected.size))
        }
    }
}

@Composable
private fun ExploreScreen(state: AppState, vm: AppViewModel, text: Strings, modifier: Modifier) {
    var query by remember { mutableStateOf("") }
    val filtered = state.rates.filter { item -> item.displayName(state.language).contains(query, true) || item.key.contains(query, true) }
    Column(modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(18.dp))
        Text(text.explore, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(text.ratesAvailable.format(state.rates.size), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text(text.searchNameCode) }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp), contentPadding = PaddingValues(vertical = 14.dp)) {
            items(filtered, key = { it.key }) { item -> SelectRow(item, item.key in state.selected, state.language, text) { vm.toggle(item.key) } }
        }
    }
}

@Composable
private fun SelectRow(item: RateItem, selected: Boolean, language: Language, text: Strings, onToggle: () -> Unit) {
    ListItem(
        headlineContent = { Text(item.displayName(language), fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text(item.key.uppercase()) },
        trailingContent = {
            IconButton(onClick = onToggle) {
                if (selected) Icon(Icons.Default.ArrowDownward, text.remove) else Icon(Icons.Default.Add, text.add)
            }
        },
        modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle)
    )
}

@Composable
private fun RateCard(item: RateItem, language: Language, text: Strings) {
    val value = item.rate.value ?: "—"
    val change = item.rate.change
    val positive = (change ?: 0.0) >= 0
    ElevatedCard(modifier = Modifier.fillMaxWidth().animateContentSize(), shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(item.displayName(language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(item.key.uppercase(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(item.symbol, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(14.dp))
            Text(formatNumber(value, language), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(if (positive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward, null, modifier = Modifier.size(16.dp))
                Text(" ${change?.let { formatDecimal(it, language) } ?: "0"}", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Text(item.rate.date ?: text.updatedRecently, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun SettingsScreen(state: AppState, vm: AppViewModel, text: Strings, modifier: Modifier) {
    Column(modifier.fillMaxSize().padding(20.dp)) {
        Spacer(Modifier.height(18.dp))
        Text(text.settings, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text(text.language, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(selected = state.language == Language.ENGLISH, onClick = { vm.setLanguage(Language.ENGLISH) }, label = { Text(text.english) })
                    FilterChip(selected = state.language == Language.PERSIAN, onClick = { vm.setLanguage(Language.PERSIAN) }, label = { Text(text.persian) })
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text(text.automaticUpdates, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text.automaticUpdatesDescription, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text(text.selectedRates, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text.selected.format(state.selected.size), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
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
