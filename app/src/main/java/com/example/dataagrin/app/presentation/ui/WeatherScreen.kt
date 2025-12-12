package com.example.dataagrin.app.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.compose.runtime.remember
import com.example.dataagrin.app.domain.model.HourlyWeather
import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.presentation.viewmodel.WeatherViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = koinViewModel()) {
    val weather by viewModel.weather.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadWeather()
    }

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
                        imageVector = Icons.Filled.Cloud,
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
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE) }
    val hasLoadedSuccessfully = remember { mutableStateOf(prefs.getBoolean("has_loaded_successfully", false)) }
    val lastApiUpdateTime = remember { mutableStateOf(prefs.getString("last_api_update", "") ?: "") }
    
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    if (weather.isFromCache) Color.Red else Color.Green
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (weather.isFromCache) "Sem Conexão" else "Conectado",
                            fontSize = 12.sp,
                            color = if (weather.isFromCache) Color.Red else Color.Green,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = getWeatherEmojiByCode(weather.weatherCode, java.time.LocalDateTime.now().hour),
                    fontSize = 80.sp
                )

                Spacer(modifier = Modifier.height(18.dp))
                Text(text = "São Paulo, SP", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(18.dp))
                Text(text = "${weather.temperature}°C", fontSize = 48.sp)

                Spacer(modifier = Modifier.height(18.dp))
                Text(text = weather.weatherDescription, fontSize = 20.sp)
                Text(text = "Umidade: ${weather.humidity}% 💧", fontSize = 16.sp)
                
                // Se conseguiu carregar com sucesso, marca flag e salva timestamp
                if (!weather.isFromCache && weather.weatherDescription != "Sem conexão") {
                    hasLoadedSuccessfully.value = true
                    val now = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                    lastApiUpdateTime.value = now
                    prefs.edit().putBoolean("has_loaded_successfully", true).apply()
                    prefs.edit().putString("last_api_update", now).apply()
                }
                
                // Sempre mostra o horário da última atualização bem-sucedida
                if (lastApiUpdateTime.value.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Última atualização: ${lastApiUpdateTime.value}", fontSize = 12.sp, color = Color.Gray)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onRefresh) {
                    Text("Atualizar")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        val currentHour = java.time.LocalDateTime.now().hour
        
        // Filtrar apenas próximas horas (validar que a hora é um número inteiro válido)
        val upcomingHours = weather.hourlyForecast.filter { hourly ->
            val hourInt = hourly.time.toIntOrNull() ?: return@filter false
            hourInt >= currentHour + 1 && hourInt < 24
        }
        
        // Se conseguiu carregar com sucesso, marca flag
        if (!weather.isFromCache && upcomingHours.isNotEmpty()) {
            hasLoadedSuccessfully.value = true
            prefs.edit().putBoolean("has_loaded_successfully", true).apply()
        }
        
        // Mostra aviso se está offline mas tem dados de previsão
        if (weather.isFromCache && upcomingHours.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("⚠️ Sem conexão - exibindo últimos dados salvos", fontSize = 12.sp, color = Color(0xFFF57F17), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Só mostra previsão SE JÁ CARREGOU COM SUCESSO
        if (hasLoadedSuccessfully.value) {
            Text("Previsão nas Próximas Horas", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Só mostra os cards se já carregou com sucesso pelo menos uma vez
        if (hasLoadedSuccessfully.value && upcomingHours.isNotEmpty()) {
            // Mostra horas futuras
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                upcomingHours.take(3).forEach { hourly ->
                    HourlyForecastItem(hourly)
                }
            }
        } else if (hasLoadedSuccessfully.value && upcomingHours.isEmpty() && weather.hourlyForecast.isNotEmpty()) {
            // Se está em cache mas sem horas futuras, mostra tudo do cache
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                weather.hourlyForecast.take(3).forEach { hourly ->
                    HourlyForecastItem(hourly)
                }
            }
        }
        
        // Mostrar mensagem APENAS se offline e nunca carregou dados antes (primeira vez)
        if (!hasLoadedSuccessfully.value && weather.isFromCache && weather.hourlyForecast.isEmpty()) {
            Text("⚠️ Sem conexão com a internet, por favor, conecte-se!", fontSize = 14.sp, color = Color(0xFFF57F17), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun HourlyForecastItem(hourly: HourlyWeather) {
    val formattedHour = "${hourly.time}:00"
    val currentHour = java.time.LocalDateTime.now().hour
    val isNextHour = hourly.time.toIntOrNull() == currentHour + 1
    
    // Destacar a próxima hora
    val backgroundColor = if (isNextHour) Color(0xFFFFE082) else Color.Transparent

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .width(100.dp)
            .height(220.dp)
            .background(backgroundColor, shape = CardDefaults.shape),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.height(16.dp)) {
                if (isNextHour) {
                    Text(text = "▼ PRÓXIMA", fontSize = 9.sp, color = Color(0xFFF57F17), fontWeight = FontWeight.Bold)
                }
            }
            
            Text(text = formattedHour, fontSize = 12.sp, fontWeight = if (isNextHour) FontWeight.Bold else FontWeight.Normal)
            
            Text(
                text = getWeatherEmojiByCode(hourly.weatherCode, hourly.time.toIntOrNull() ?: 0),
                fontSize = 28.sp
            )
            
            Text(text = "${hourly.temperature}°C", fontSize = 14.sp, fontWeight = if (isNextHour) FontWeight.Bold else FontWeight.Normal)
            
            if (hourly.humidity > 0) {
                Text(text = "${hourly.humidity}% 💧", fontSize = 12.sp, color = Color.Gray)
            }
            
            if (hourly.description.isNotEmpty()) {
                Text(text = hourly.description, fontSize = 12.sp, color = Color.Gray, maxLines = 3)
            }
        }
    }
}

@Composable
private fun getWeatherIcon(weatherDescription: String): ImageVector {
    return when {
        weatherDescription.contains("sol", ignoreCase = true) || weatherDescription.contains("limpo", ignoreCase = true) -> Icons.Filled.WbSunny
        weatherDescription.contains("nuvem", ignoreCase = true) -> Icons.Filled.Cloud
        weatherDescription.contains("chuva", ignoreCase = true) -> Icons.Filled.CloudQueue
        weatherDescription.contains("drizzle", ignoreCase = true) || weatherDescription.contains("chuvisco", ignoreCase = true) -> Icons.Filled.Grain
        else -> Icons.Filled.Cloud
    }
}

private fun getWeatherEmojiByCode(code: Int, forecastHour: Int = 0): String {
    val isNight = forecastHour < 6 || forecastHour >= 18 // Noite: 18h - 6h
    
    return when (code) {
        0 -> if (isNight) "🌙" else "☀️" // Céu limpo
        1, 2, 3 -> if (isNight) "🌙☁️" else "⛅" // Parcialmente nublado (lua/sol com nuvem)
        45, 48 -> "🌫️" // Nevoeiro
        51, 53, 55 -> "🌧️" // Chuvisco
        61, 63, 65 -> "🌧️" // Chuva
        80, 81, 82 -> "⛈️" // Pancadas de chuva
        85, 86 -> "⛈️" // Chuva forte
        95, 96, 99 -> "⚡" // Tempestade
        else -> "☁️" // Desconhecido
    }
}

private fun getWeatherIconByCode(code: Int): ImageVector {
    return when (code) {
        0 -> Icons.Filled.WbSunny // Céu limpo
        1, 2, 3 -> Icons.Filled.Cloud // Parcialmente nublado / nublado
        45, 48 -> Icons.Filled.Cloud // Nevoeiro
        51, 53, 55 -> Icons.Filled.Grain // Chuvisco
        61, 63, 65 -> Icons.Filled.CloudQueue // Chuva
        80, 81, 82 -> Icons.Filled.CloudQueue // Pancadas de chuva
        85, 86 -> Icons.Filled.CloudQueue // Chuva forte
        95, 96, 99 -> Icons.Filled.Cloud // Tempestade
        else -> Icons.Filled.Cloud // Desconhecido
    }
}
