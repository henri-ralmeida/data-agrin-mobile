package com.example.dataagrin.app.presentation.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.GpsOff
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.location.LocationManagerCompat
import com.example.dataagrin.app.domain.model.HourlyWeather
import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.presentation.ui.components.GenericHeader
import com.example.dataagrin.app.presentation.viewmodel.LocationState
import com.example.dataagrin.app.presentation.viewmodel.WeatherViewModel
import com.example.dataagrin.app.ui.theme.AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel
import androidx.core.content.edit

// --- Pequenos blocos de UI reutilizáveis ---
@Composable
fun PermissionDeniedMessage(
    modifier: Modifier = Modifier,
    onPermissionGranted: () -> Unit = {},
) {
    val colors = AppTheme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = colors.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Permissão de localização necessária",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Para obter dados climáticos precisos, permita o acesso à localização",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onPermissionGranted,
            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text("Conceder permissão", color = colors.buttonText)
        }
    }
}

@Composable
fun NoLocationMessage(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
) {
    val colors = AppTheme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(imageVector = Icons.Filled.GpsOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = colors.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Geolocalização inativa", fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(
            "Ative o GPS/serviço de localização para que possamos obter sua posição.",
            fontSize = 12.sp,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = colors.primary), shape = RoundedCornerShape(8.dp)) {
            Text("Tentar novamente", color = colors.buttonText)
        }
    }
}

@Composable
fun NoConnectionMessage(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
) {
    val colors = AppTheme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(imageVector = Icons.Filled.CloudOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Sem conexão com a Internet", fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(
            "Conecte-se à internet para atualizar os dados climáticos.",
            fontSize = 12.sp,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = colors.primary), shape = RoundedCornerShape(8.dp)) {
            Text("Tentar novamente", color = colors.buttonText)
        }
    }
}

// --- Tela principal ---
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = koinViewModel(),
    isExpandedScreen: Boolean = false,
) {
    val weather by viewModel.weather.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val locationState by viewModel.locationState.collectAsState()
    val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsState()

    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val colors = AppTheme.colors

    // animação de pulso para carregamento
    val pulseAlpha by animateFloatAsState(
        targetValue = if (isLoading) 0.7f else 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1000), repeatMode = RepeatMode.Reverse),
    )

    // Verificar se os serviços de localização do dispositivo estão ativados (usa auxiliar de compatibilidade)
    fun isDeviceLocationEnabled(ctx: Context): Boolean {
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return false
        return try {
            LocationManagerCompat.isLocationEnabled(lm)
        } catch (_: Exception) {
            false
        }
    }

    // Launcher para abrir configurações de localização e reagir quando o usuário retornar
    val locationSettingsLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            // callback ao retornar das configurações: verificar automaticamente se localização foi ativada
            if (isDeviceLocationEnabled(context)) {
                // Localização foi ativada! Avançar automaticamente para carregamento
                viewModel.loadWeather()
            }
            // Se não foi ativada, permanece na tela de localização inativa
        }

    // Considerar localização indisponível se for apenas do cache – exigir um Disponível fresco
    val locationAvailable =
        when (val ls = locationState) {
            is LocationState.Available -> !ls.isFromCache
            else -> false
        }
    val hasNetwork = isNetworkAvailable
    val permissionGranted = locationPermissionState.status == PermissionStatus.Granted
    val locationEnabled = remember { mutableStateOf(isDeviceLocationEnabled(context)) }

    // Atualiza o state do GPS quando muda
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000) // Verifica a cada segundo
            val current = isDeviceLocationEnabled(context)
            if (locationEnabled.value != current) {
                locationEnabled.value = current
            }
        }
    }

    // Quando tudo estiver OK (permissão, localização, internet), carregar automaticamente
    LaunchedEffect(permissionGranted, locationEnabled.value, hasNetwork, weather, isLoading) {
        if (permissionGranted && isDeviceLocationEnabled(context) && hasNetwork && weather == null && !isLoading) {
            viewModel.loadWeather()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when {
                weather != null -> {
                    // Cabeçalho (o badge flutuante de conexão aparecerá abaixo)
                    if (!isExpandedScreen) {
                        GenericHeader(
                            title = "Clima",
                            subtitle = "Acompanhe as condições climáticas",
                            emoji = "☁️",
                            backgroundColor = colors.headerBackground,
                            titleColor = colors.buttonText,
                            emojiAlpha = 0.6f,
                        )
                    }
                    WeatherContentWrapper(
                        weather = weather!!,
                        onRefresh = viewModel::loadWeather,
                        isConnected = hasNetwork,
                        isLocationEnabled = locationEnabled.value,
                    )
                }
                !permissionGranted -> {
                    PermissionDeniedMessage(
                        modifier = Modifier.fillMaxWidth(),
                        onPermissionGranted = { locationPermissionState.launchPermissionRequest() },
                    )
                }
                !isDeviceLocationEnabled(context) -> {
                    NoLocationMessage(modifier = Modifier.fillMaxWidth(), onRetry = {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        locationSettingsLauncher.launch(intent)
                    })
                }
                !hasNetwork -> {
                    NoConnectionMessage(modifier = Modifier.fillMaxWidth(), onRetry = {
                        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        context.startActivity(intent)
                    })
                }
                !locationAvailable -> {
                    // Localização do dispositivo está ligada, mas ainda não conseguiu obter coordenadas
                    // Mostrar loading enquanto tenta obter localização
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(modifier = Modifier.graphicsLayer(alpha = pulseAlpha))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Obtendo localização...", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(modifier = Modifier.graphicsLayer(alpha = pulseAlpha))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Carregando dados de clima...", fontSize = 14.sp, color = colors.textSecondary)
                        }
                    }
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Sem dados de clima disponíveis", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Verifique sua conexão", fontSize = 12.sp, color = colors.textSecondary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = viewModel::loadWeather,
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text("Tentar novamente", color = colors.buttonText)
                            }
                        }
                    }
                }
            }
        }

        // Badges flutuantes - aparecem apenas quando há dados de clima
        if (weather != null && !isExpandedScreen) {
            // Badge de conexão
            ConnectionFloatingBadge(
                isConnected = hasNetwork,
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 120.dp, end = 16.dp),
            )
        }
    }
}

@Composable
fun ConnectionFloatingBadge(
    isConnected: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Box(
                modifier =
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isConnected) colors.statusCompleted else Color.Red),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isConnected) "Conectado" else "Sem Conexão",
                color = if (isConnected) colors.statusCompleted else Color.Red,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun WeatherContentWrapper(
    weather: Weather,
    onRefresh: () -> Unit,
    isConnected: Boolean = true,
    isLocationEnabled: Boolean = true,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        WeatherContent(weather, onRefresh, isConnected, isLocationEnabled)
    }
}

@Composable
fun WeatherContent(
    weather: Weather,
    onRefresh: () -> Unit,
    isConnected: Boolean = true,
    isLocationEnabled: Boolean = true,
) {
    val colors = AppTheme.colors
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE) }
    val hasLoadedSuccessfully = remember { mutableStateOf(prefs.getBoolean("has_loaded_successfully", false)) }
    val lastApiUpdateTime = remember { mutableStateOf(prefs.getString("last_api_update", "") ?: "") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .fillMaxWidth()
                .verticalScroll(androidx.compose.foundation.rememberScrollState()),
    ) {
        // Emoji centralizado com status ao lado (na mesma linha)
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
        ) {
            Text(
                text =
                    getWeatherEmojiByCode(
                        weather.weatherCode,
                        java.time.LocalDateTime
                            .now()
                            .hour,
                    ),
                fontSize = 80.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
            )

            // Removido badge inline de status de cache/fonte para manter telas limitadas
        }

        // Nome da cidade com badge de conexão ao lado
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = weather.cityName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            // Pequeno badge: ponto + texto (mantém compacto para não perturbar o layout)
        }

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
            prefs.edit {putBoolean("has_loaded_successfully", true) }
            prefs.edit { putString("last_api_update", now) }
        }

        // Sempre mostra o horário da última atualização bem-sucedida
        if (lastApiUpdateTime.value.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Última atualização: ${lastApiUpdateTime.value}", fontSize = 11.sp, color = colors.textTertiary)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRefresh,
            colors = ButtonDefaults.buttonColors(containerColor = colors.headerBackground),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(36.dp),
        ) {
            Text("Atualizar", fontSize = 14.sp, color = Color.White)
        }

        // Calcula a próxima hora cheia (arredonda para cima)
        val now = java.time.LocalDateTime.now()
        val nextHour = (now.hour + 1) % 24 // Próxima hora (0-23)

        // Quando tem dados de clima, pega as próximas 24 horas a partir da próxima hora
        val allHours = weather.hourlyForecast
        val startIndex = allHours.indexOfFirst { (it.time.toIntOrNull() ?: 0) == nextHour }
        val upcomingHours =
            if (startIndex != -1) {
                // Pega 24 horas a partir do startIndex, fazendo wrap-around se necessário
                val fromStart = allHours.drop(startIndex)
                val needed = 24 - fromStart.size
                if (needed > 0) {
                    fromStart + allHours.take(needed)
                } else {
                    fromStart.take(24)
                }
            } else {
                // Fallback: pega as primeiras 24
                allHours.take(24)
            }

        // Se conseguiu carregar com sucesso, marca flag
        if (!weather.isFromCache && upcomingHours.isNotEmpty()) {
            hasLoadedSuccessfully.value = true
            prefs.edit { putBoolean("has_loaded_successfully", true)}
        }

        // Mostra aviso se está offline mas tem dados de previsão
        if ((!isConnected || weather.isFromCache) && upcomingHours.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("⚠️ Sem Conexão", fontSize = 14.sp, color = colors.warning, fontWeight = FontWeight.Bold)
            Text("Exibindo últimos dados salvos", fontSize = 12.sp, color = colors.warning)
        }

        // Mostra aviso se geolocalização está desabilitada mas tem dados
        if (!isLocationEnabled && upcomingHours.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("⚠️ Sem Geolocalização", fontSize = 14.sp, color = colors.warning, fontWeight = FontWeight.Bold)
            Text("Exibindo última localização salva", fontSize = 12.sp, color = colors.warning)
        }

        // Sempre mostra previsão das próximas 24 horas em uma LazyRow (carousel)
        if (upcomingHours.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Previsão nas Próximas 24 Horas", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.alpha(0.6f),
                ) {
                    Text("Arraste", fontSize = 11.sp, color = Color.Gray)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Arraste para ver mais",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
            ) {
                items(upcomingHours) { hourly ->
                    val isNextHour = hourly.time.toIntOrNull() == nextHour
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn(),
                    ) {
                        HourlyForecastItem(hourly, isNextHour)
                    }
                }
                // Card final indicando fim (ajustado para combinar com os blocos)
                item {
                    Box(
                        modifier =
                            Modifier
                                .width(104.dp)
                                .height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text("🌅", fontSize = 22.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Fim da\nprevisão",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        } else if (hasLoadedSuccessfully.value && upcomingHours.isEmpty() && weather.hourlyForecast.isNotEmpty()) {
            // Se está em cache mas sem horas futuras, mostra apenas 3 horas do cache
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                weather.hourlyForecast.take(3).forEach { hourly ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn(),
                    ) {
                        HourlyForecastItem(hourly)
                    }
                }
            }
        }

        // Mostrar mensagem APENAS se offline e nunca carregou dados antes (primeira vez)
        if (!hasLoadedSuccessfully.value && weather.isFromCache && weather.hourlyForecast.isEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "⚠️ Sem conexão com a internet, por favor, conecte-se!",
                fontSize = 13.sp,
                color = colors.warning,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun HourlyForecastItem(
    hourly: HourlyWeather,
    isHighlighted: Boolean = false,
) {
    val colors = AppTheme.colors
    val formattedHour = "${hourly.time}:00"
    val currentHour =
        java.time.LocalDateTime
            .now()
            .hour
    val isNextHour = isHighlighted || hourly.time.toIntOrNull() == currentHour + 1

    // Destacar a próxima hora com o tom antigo (melhor contraste)
    val backgroundColor = if (isNextHour) Color(0xFFFFD54F) else colors.surface

    // Box externo para permitir badge "pregada" fora do card branco
    Box(
        modifier =
            Modifier
                .width(104.dp)
                .height(200.dp),
    ) {
        // Badge "placa" pregada por cima (fora do card)
        if (isNextHour) {
            Box(modifier = Modifier.align(Alignment.TopCenter).zIndex(1f)) {
                // Pequeno "prego" circular (menor)
                Box(
                    modifier =
                        Modifier
                            .size(6.dp)
                            .background(colors.textSecondary, CircleShape)
                            .align(Alignment.TopCenter)
                            .offset(y = (-12).dp),
                )

                // Placa laranja (mais reduzida)
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-8).dp)
                            .background(colors.statusInProgress, RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 2.dp),
                ) {
                    Text(text = "PRÓXIMA", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Card deslocado para baixo para abrir espaço para a placa (ajustado para ficar mais próximo ao topo)
        Card(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 6.dp)
                    .width(104.dp)
                    .height(180.dp),
            elevation = CardDefaults.cardElevation(2.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .background(backgroundColor, shape = CardDefaults.shape)
                        .padding(top = 12.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)
                        .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Hora (centralizada) — posicionada abaixo da placa
                Text(
                    text = formattedHour,
                    fontSize = 13.sp,
                    fontWeight = if (isNextHour) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = if (isSystemInDarkTheme() && isNextHour) Color(0xFFFF9800) else colors.textPrimary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Emoji
                Text(
                    text = getWeatherEmojiByCode(hourly.weatherCode, hourly.time.toIntOrNull() ?: 0),
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Temperatura (centralizada)
                Text(
                    text = "${hourly.temperature}°C",
                    fontSize = 15.sp,
                    fontWeight = if (isNextHour) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = if (isSystemInDarkTheme() && isNextHour) Color(0xFFFF9800) else colors.textPrimary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Conteúdo variável (descrição) - usa weight para ocupar espaço disponível
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Descrição
                    if (hourly.description.isNotEmpty()) {
                        Text(
                            text = hourly.description,
                            fontSize = 9.sp,
                            color = if (isSystemInDarkTheme() && isNextHour) Color(0xFFFF9800) else colors.textSecondary,
                            maxLines = 2,
                            textAlign = TextAlign.Center,
                            lineHeight = 12.sp,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                    }
                }

                // Umidade sempre fixada na parte inferior
                if (hourly.humidity > 0) {
                    Text(
                        text = "${hourly.humidity}% 💧",
                        fontSize = 11.sp,
                        color = colors.textTertiary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

private fun getWeatherEmojiByCode(
    code: Int,
    forecastHour: Int = 0,
): String {
    val isNight = forecastHour !in 6..<18 // Noite: 18h - 6h

    return when (code) {
        0 -> if (isNight) "🌙" else "☀️" // Céu limpo
        1, 2, 3 -> if (isNight) "☁️" else "⛅" // Parcialmente nublado (lua/sol com nuvem)
        45, 48 -> "☁️" // Nevoeiro
        51, 53, 55 -> "🌧️" // Chuvisco
        61, 63, 65 -> "🌧️" // Chuva
        80, 81, 82 -> "⛈️" // Pancadas de chuva
        85, 86 -> "⛈️" // Chuva forte
        95, 96, 99 -> "⚡" // Tempestade
        else -> "☁️" // Desconhecido
    }
}
