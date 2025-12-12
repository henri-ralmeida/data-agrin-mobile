package com.example.dataagrin.app.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import com.example.dataagrin.app.domain.model.HourlyWeather
import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.presentation.viewmodel.WeatherViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = koinViewModel(),
    isExpandedScreen: Boolean = false
) {
    val weather by viewModel.weather.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val pulseAlpha by animateFloatAsState(
        targetValue = if (isLoading) 0.7f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        viewModel.loadWeather()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        WeatherScreenHeader()

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.graphicsLayer(alpha = pulseAlpha)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Carregando dados de clima...", fontSize = 14.sp, color = Color.Gray)
                    }
                }
                weather != null -> {
                    WeatherContentWrapper(
                        weather = weather!!, 
                        onRefresh = viewModel::loadWeather,
                        isExpandedScreen = isExpandedScreen
                    )
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
                        Button(
                            onClick = viewModel::loadWeather,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherScreenHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B5E20))
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Clima",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Acompanhe as condições climáticas",
                fontSize = 14.sp,
                color = Color(0xFFE8F5E9),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Emoji decorativo de nuvem
        Text(
            "☁️",
            fontSize = 64.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .alpha(0.6f)
        )
    }
}

@Composable
fun WeatherContentWrapper(
    weather: Weather, 
    onRefresh: () -> Unit,
    isExpandedScreen: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        WeatherContent(weather, onRefresh, isExpandedScreen)
    }
}

@Composable
fun WeatherContent(
    weather: Weather, 
    onRefresh: () -> Unit,
    isExpandedScreen: Boolean = false
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE) }
    val hasLoadedSuccessfully = remember { mutableStateOf(prefs.getBoolean("has_loaded_successfully", false)) }
    val lastApiUpdateTime = remember { mutableStateOf(prefs.getString("last_api_update", "") ?: "") }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(60.dp))
            Text(
                text = getWeatherEmojiByCode(weather.weatherCode, java.time.LocalDateTime.now().hour),
                fontSize = 60.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(100.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (weather.isFromCache) Color.Red else Color.Green
                        )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (weather.isFromCache) "Sem Conexão" else "Conectado",
                    fontSize = 11.sp,
                    color = if (weather.isFromCache) Color.Red else Color.Green,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }

        Text(text = "São Paulo, SP", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "${weather.temperature}°C", fontSize = 38.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))
        Text(text = weather.weatherDescription, fontSize = 16.sp)
        Text(text = "Umidade: ${weather.humidity}% 💧", fontSize = 14.sp, color = Color.Gray)
        
        // Se conseguiu carregar com sucesso, marca flag e salva timestamp
        if (!weather.isFromCache && weather.weatherDescription != "Sem conexão") {
            hasLoadedSuccessfully.value = true
            val now = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            lastApiUpdateTime.value = now
            prefs.edit { putBoolean(/* p0 = */ "has_loaded_successfully", /* p1 = */ true) }
            prefs.edit {putString("last_api_update", now)}
        }
        
        // Sempre mostra o horário da última atualização bem-sucedida
        if (lastApiUpdateTime.value.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Última atualização: ${lastApiUpdateTime.value}", fontSize = 11.sp, color = Color.Gray)
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRefresh,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text("Atualizar", fontSize = 14.sp)
        }
        
        val currentHour = java.time.LocalDateTime.now().hour
        val upcomingHours = weather.hourlyForecast.filter { hourly ->
            val hourInt = hourly.time.toIntOrNull() ?: return@filter false
            hourInt >= currentHour + 1 && hourInt < 24
        }
        
        // Se conseguiu carregar com sucesso, marca flag
        if (!weather.isFromCache && upcomingHours.isNotEmpty()) {
            hasLoadedSuccessfully.value = true
            prefs.edit { putBoolean("has_loaded_successfully", true) }
        }
        
        // Mostra aviso se está offline mas tem dados de previsão
        if (weather.isFromCache && upcomingHours.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("⚠️ Sem Conexão", fontSize = 14.sp, color = Color(0xFFF57F17), fontWeight = FontWeight.Bold)
            Text("Exibindo últimos dados salvos", fontSize = 12.sp, color = Color(0xFFF57F17))
        }
        
        // Só mostra previsão SE JÁ CARREGOU COM SUCESSO
        if (hasLoadedSuccessfully.value) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Previsão nas Próximas Horas", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Só mostra os cards se já carregou com sucesso pelo menos uma vez
        if (hasLoadedSuccessfully.value && upcomingHours.isNotEmpty()) {
            // Mostra mais horas em telas expandidas
            val hoursToShow = if (isExpandedScreen) 6 else 3
            val hoursToDisplay = upcomingHours.take(hoursToShow)
            
            if (isExpandedScreen && hoursToDisplay.size > 3) {
                // Layout em grid para telas expandidas (2 linhas de 3)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        hoursToDisplay.take(3).forEach { hourly ->
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn()
                            ) {
                                HourlyForecastItem(hourly)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        hoursToDisplay.drop(3).forEach { hourly ->
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn()
                            ) {
                                HourlyForecastItem(hourly)
                            }
                        }
                    }
                }
            } else {
                // Layout em linha única para smartphones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    hoursToDisplay.forEach { hourly ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn()
                        ) {
                            HourlyForecastItem(hourly)
                        }
                    }
                }
            }
        } else if (hasLoadedSuccessfully.value && upcomingHours.isEmpty() && weather.hourlyForecast.isNotEmpty()) {
            // Se está em cache mas sem horas futuras, mostra apenas 3 horas do cache
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                weather.hourlyForecast.take(3).forEach { hourly ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn()
                    ) {
                        HourlyForecastItem(hourly)
                    }
                }
            }
        }
        
        // Mostrar mensagem APENAS se offline e nunca carregou dados antes (primeira vez)
        if (!hasLoadedSuccessfully.value && weather.isFromCache && weather.hourlyForecast.isEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("⚠️ Sem conexão com a internet, por favor, conecte-se!", fontSize = 13.sp, color = Color(0xFFF57F17), fontWeight = FontWeight.SemiBold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun HourlyForecastItem(hourly: HourlyWeather) {
    val formattedHour = "${hourly.time}:00"
    val currentHour = java.time.LocalDateTime.now().hour
    val isNextHour = hourly.time.toIntOrNull() == currentHour + 1
    
    // Destacar a próxima hora
    val backgroundColor = if (isNextHour) Color(0xFFFFE082) else Color(0xFFF5F5F5)

    Card(
        modifier = Modifier
            .width(120.dp)
            .height(195.dp) // Altura maior para caber texto
            .padding(horizontal = 4.dp)
            .background(backgroundColor, shape = CardDefaults.shape),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Parte superior
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.height(18.dp)) {
                    if (isNextHour) {
                        Text(text = "▼ PRÓXIMA", fontSize = 10.sp, color = Color(0xFFF57F17), fontWeight = FontWeight.Bold)
                    }
                }
                
                Text(text = formattedHour, fontSize = 13.sp, fontWeight = if (isNextHour) FontWeight.Bold else FontWeight.Normal)
                
                Text(
                    text = getWeatherEmojiByCode(hourly.weatherCode, hourly.time.toIntOrNull() ?: 0),
                    fontSize = 32.sp
                )
                
                Text(text = "${hourly.temperature}°C", fontSize = 15.sp, fontWeight = if (isNextHour) FontWeight.Bold else FontWeight.Normal)
            }
            
            // Parte inferior com altura fixa
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.height(55.dp) // Altura aumentada para descrição + umidade
            ) {
                if (hourly.humidity > 0) {
                    Text(text = "${hourly.humidity}% 💧", fontSize = 12.sp, color = Color.Gray)
                }
                
                if (hourly.description.isNotEmpty()) {
                    Text(
                        text = hourly.description, 
                        fontSize = 10.sp, 
                        color = Color.Gray, 
                        maxLines = 2, 
                        textAlign = TextAlign.Center,
                        lineHeight = 12.sp,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }
    }
}

private fun getWeatherEmojiByCode(code: Int, forecastHour: Int = 0): String {
    val isNight = forecastHour !in 6..<18 // Noite: 18h - 6h
    
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
