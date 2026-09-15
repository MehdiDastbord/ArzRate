package com.arz.rates

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arz.rates.ui.AppViewModel
import com.arz.rates.ui.ArzApp
import com.arz.rates.ui.theme.ArzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: AppViewModel = viewModel()
            val state = vm.state.collectAsStateWithLifecycle()
            ArzTheme(state.value.theme) {
                ArzApp(vm)
            }
        }
    }
}
