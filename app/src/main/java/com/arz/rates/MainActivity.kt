package com.arz.rates

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.arz.rates.ui.ArzApp
import com.arz.rates.ui.theme.ArzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArzTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ArzApp()
                }
            }
        }
    }
}
