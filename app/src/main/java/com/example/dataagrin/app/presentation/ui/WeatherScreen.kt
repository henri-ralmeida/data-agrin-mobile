package com.example.dataagrin.app.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.presentation.viewmodel.WeatherViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = koinViewModel()) {
    val weather by viewModel.weather.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        weather?.let {
            WeatherContent(it, onRefresh = viewModel::loadWeather)
        } ?: CircularProgressIndicator()
    }
}

@Composable
fun WeatherContent(weather: Weather, onRefresh: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Clima Agora", fontSize = 24.sp)
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (weather.isFromCache) Color.Yellow else Color.Green)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "${weather.temperature}°C", fontSize = 48.sp)
            Text(text = weather.weatherDescription, fontSize = 20.sp)
            Text(text = "Umidade: ${weather.humidity}%", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onRefresh) {
                Text("Atualizar")
            }
        }
    }
}