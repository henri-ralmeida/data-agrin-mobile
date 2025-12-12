package com.example.dataagrin.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.example.dataagrin.app.data.firebase.FirebaseManager
import com.example.dataagrin.app.presentation.ui.AppNavigation
import com.example.dataagrin.app.ui.theme.DataAgrinMobileTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar Firebase
        FirebaseManager.initialize()
        
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            DataAgrinMobileTheme {
                AppNavigation(windowSizeClass = windowSizeClass)
            }
        }
    }
}