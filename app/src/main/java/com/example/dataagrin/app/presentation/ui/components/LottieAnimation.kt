package com.example.dataagrin.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LottieTaskAnimation(modifier: Modifier = Modifier, animationName: String) {
    Box(
        modifier = modifier
            .size(100.dp)
            .background(Color.LightGray, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(animationName, fontSize = 12.sp, color = Color.DarkGray)
    }
}
