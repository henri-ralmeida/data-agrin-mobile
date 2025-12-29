package com.example.dataagrin.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ComposeFoundationFlags
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.example.dataagrin.app.data.firebase.FirebaseManager
import com.example.dataagrin.app.presentation.ui.AppNavigation
import com.example.dataagrin.app.ui.theme.DataAgrinMobileTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseManager.initialize()

        // Temporário: opt-out do novo comportamento de clickable para compatibilidade
        ComposeFoundationFlags.isNonComposedClickableEnabled = true

        // Removido: solicitação de permissão de localização na inicialização

        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            DataAgrinMobileTheme {
                AppNavigation(windowSizeClass = windowSizeClass)
            }
        }
    }
}
