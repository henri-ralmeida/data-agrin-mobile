package com.example.dataagrin.app.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dataagrin.app.domain.model.HourlyWeather
import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.presentation.viewmodel.WeatherViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = koinViewModel()) {
    val weather by viewModel.weather.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Carregando dados de clima...", fontSize = 14.sp, color = Color.Gray)
                }
            }
            weather != null -> {
                WeatherContent(weather!!, onRefresh = viewModel::loadWeather)
            }
            else -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Cloud,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Sem dados de clima disponíveis", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Verifique sua conexão", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = viewModel::loadWeather) {
                        Text("Tentar novamente")
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherContent(weather: Weather, onRefresh: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Clima Agora", fontSize = 24.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    if (weather.isFromCache) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color.Yellow)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color.Green)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Icon(imageVector = getWeatherIcon(weather.weatherDescription), contentDescription = null, modifier = Modifier.size(96.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "São Paulo, SP", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "${weather.temperature}°C", fontSize = 48.sp)
                Text(text = weather.weatherDescription, fontSize = 20.sp)
                Text(text = "Umidade: ${weather.humidity}%", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onRefresh) {
                    Text("Atualizar")
                }
            }
        }

        if (!weather.isFromCache) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Próximas Horas", fontSize = 20.sp)
            LazyRow(modifier = Modifier.padding(top = 8.dp)) {
                items(weather.hourlyForecast, key = { it.time }) { hourly ->
                    HourlyForecastItem(hourly)
                }
            }
        }
    }
}

@Composable
fun HourlyForecastItem(hourly: HourlyWeather) {
    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = hourly.time, fontSize = 14.sp)
            Text(text = "${hourly.temperature}°C", fontSize = 18.sp)
        }
    }
}

@Composable
private fun getWeatherIcon(weatherDescription: String): ImageVector {
    return when {
        weatherDescription.contains("sol", ignoreCase = true) || weatherDescription.contains("limpo", ignoreCase = true) -> Icons.Filled.WbSunny
        weatherDescription.contains("chuva", ignoreCase = true) || weatherDescription.contains("pancada", ignoreCase = true) -> Icons.Filled.CloudQueue
        weatherDescription.contains("chuvisco", ignoreCase = true) || weatherDescription.contains("neblina", ignoreCase = true) -> Icons.Filled.Grain
        weatherDescription.contains("nuvem", ignoreCase = true) || weatherDescription.contains("nublado", ignoreCase = true) -> Icons.Filled.Cloud
        else -> Icons.Filled.WbSunny
    }
}
