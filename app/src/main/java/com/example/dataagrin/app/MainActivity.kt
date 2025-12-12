package com.example.dataagrin.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dataagrin.app.data.firebase.FirebaseManager
import com.example.dataagrin.app.presentation.ui.AppNavigation
import com.example.dataagrin.app.ui.theme.DataAgrinMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar Firebase
        FirebaseManager.initialize()
        
        enableEdgeToEdge()
        setContent {
            DataAgrinMobileTheme {
                AppNavigation()
            }
        }
    }
}