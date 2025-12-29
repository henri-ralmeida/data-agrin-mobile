package com.example.dataagrin.app.presentation.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.example.dataagrin.app.domain.model.TaskRegistry
import com.example.dataagrin.app.presentation.ui.components.AutocompleteTextField
import com.example.dataagrin.app.presentation.ui.components.DetailItemWithIcon
import com.example.dataagrin.app.presentation.ui.components.GenericHeader
import com.example.dataagrin.app.presentation.ui.components.TimeInputField
import com.example.dataagrin.app.presentation.ui.components.formatTimeValue
import com.example.dataagrin.app.presentation.ui.utils.TimeValidation
import com.example.dataagrin.app.presentation.viewmodel.TaskRegistryViewModel
import com.example.dataagrin.app.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

// Objeto para gerenciar o histórico de atividades
object ActivityHistoryManager : GenericHistoryManager("activity_history_prefs", "activity_history")

// Objeto para gerenciar o histórico de áreas/talhões
object AreaHistoryManager : GenericHistoryManager("area_history_prefs", "area_history")

// Classe genérica para gerenciar histórico
open class GenericHistoryManager(
    private val prefsName: String,
    private val key: String,
    private val maxHistorySize: Int = 20,
    private val maxSuggestions: Int = 5,
) {
    fun getHistory(context: Context): List<String> {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val historyString = prefs.getString(key, "") ?: ""
        return if (historyString.isEmpty()) {
            emptyList()
        } else {
            historyString.split("|||").filter { it.isNotBlank() }
        }
    }

    fun addToHistory(
        context: Context,
        item: String,
    ) {
        if (item.isBlank()) return

        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val currentHistory = getHistory(context).toMutableList()

        // Remove se já existe (para reordenar)
        currentHistory.remove(item)

        // Adiciona no início
        currentHistory.add(0, item)

        // Limita o tamanho
        val trimmedHistory = currentHistory.take(maxHistorySize)

        // Salva
        prefs.edit { putString(key, trimmedHistory.joinToString("|||")) }
    }

    fun getRecentSuggestions(context: Context): List<String> = getHistory(context).take(maxSuggestions)

    fun getFilteredSuggestions(
        context: Context,
        query: String,
    ): List<String> {
        if (query.isBlank()) return emptyList()
        return getHistory(context)
            .filter {
                it.contains(query, ignoreCase = true)
            }.take(maxSuggestions)
    }
}

@Composable
fun TaskRegistryScreen(
    viewModel: TaskRegistryViewModel = koinViewModel(),
    isExpandedScreen: Boolean = false,
) {
    val taskRegistries by viewModel.taskRegistries.collectAsState()
    val colors = AppTheme.colors

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(colors.background),
    ) {
        // Header
        if (!isExpandedScreen) {
            GenericHeader(
                title = "Registros de Tarefas",
                subtitle = "Registre e acompanhe todas as atividades",
                emoji = "📓",
            )
        }

        if (isExpandedScreen) {
            // Layout lado a lado para tablets/landscape
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Formulário à esquerda com scroll
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                ) {
                    TaskRegistryForm(onInsertTaskRegistry = viewModel::insertTaskRegistry)
                }

                // Histórico à direita
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    TaskRegistryHistoryHeader(count = taskRegistries.size)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(taskRegistries, key = { it.id }) { taskRegistry ->
                            TaskRegistryItem(taskRegistry = taskRegistry)
                        }
                    }
                }
            }
        } else {
            // Layout em lista para smartphones
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { -100 }),
                    ) {
                        TaskRegistryForm(onInsertTaskRegistry = viewModel::insertTaskRegistry)
                    }
                }

                item {
                    TaskRegistryHistoryHeader(count = taskRegistries.size)
                }

                items(taskRegistries, key = { it.id }) { taskRegistry ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn(),
                        exit = slideOutVertically() + fadeOut(),
                    ) {
                        TaskRegistryItem(taskRegistry = taskRegistry)
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRegistryHistoryHeader(count: Int) {
    val colors = AppTheme.colors
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                "Histórico de Registro de Tarefas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primary,
            )
            Text(
                "$count atividade${if (count != 1) "s" else ""} registrada${if (count != 1) "s" else ""}",
                fontSize = 12.sp,
                color = Color.Gray,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskRegistryForm(onInsertTaskRegistry: (TaskRegistry) -> Unit) {
    val context = LocalContext.current
    var type by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(true) }
    var saveButtonPressed by remember { mutableStateOf(false) }

    // Histórico de atividades
    var recentActivities by remember { mutableStateOf(ActivityHistoryManager.getRecentSuggestions(context)) }

    val saveScale by animateFloatAsState(
        targetValue = if (saveButtonPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.8f),
    )

    val colors = AppTheme.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Header da forma
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        Icons.Filled.AddCircle,
                        contentDescription = "Adicionar atividade",
                        modifier = Modifier.size(24.dp),
                        tint = if (isSystemInDarkTheme()) Color.White else colors.primary,
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        "Registrar Tarefa",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSystemInDarkTheme()) Color.White else colors.primary,
                    )
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        Icons.Filled.ExpandLess,
                        contentDescription = "Expandir/Recolher",
                        tint = Color.Gray,
                        modifier =
                            Modifier
                                .size(20.dp)
                                .let {
                                    if (!isExpanded) {
                                        it
                                    } else {
                                        it
                                    }
                                },
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // === CHIPS DE SUGESTÕES RÁPIDAS ===
                if (recentActivities.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp),
                    ) {
                        Icon(
                            Icons.Filled.History,
                            contentDescription = "Histórico",
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Sugestões rápidas:",
                            fontSize = 12.sp,
                            color = Color.Gray,
                        )
                    }

                    FlowRow(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        recentActivities.forEach { activity ->
                            Row(
                                modifier =
                                    Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable {
                                            type = activity
                                        }.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = "Editar",
                                    modifier = Modifier.size(12.dp),
                                    tint = colors.primary,
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    activity,
                                    fontSize = 13.sp,
                                    color = if (isSystemInDarkTheme()) Color.White else colors.primary,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }
                }

                // === CAMPO DE TIPO COM AUTOCOMPLETE ===
                AutocompleteTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = "Tipo de Atividade",
                    placeholder = "Ex: Plantio, Pulverização",
                    getFilteredSuggestions = { query -> ActivityHistoryManager.getFilteredSuggestions(context, query) },
                    dropdownIcon = Icons.Filled.Edit,
                )
                Spacer(modifier = Modifier.height(8.dp))

                AutocompleteTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = "Talhão/Área",
                    placeholder = "Ex: Talhão 10",
                    getFilteredSuggestions = { query -> AreaHistoryManager.getFilteredSuggestions(context, query) },
                    dropdownIcon = Icons.Filled.Terrain,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TimeInputField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = "Início",
                        modifier = Modifier.weight(1f),
                    )

                    TimeInputField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = "Término",
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = observations,
                    onValueChange = { observations = it },
                    label = { Text("Observações") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Digite suas observações...") },
                    minLines = 3,
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            focusedLabelColor = colors.primary,
                        ),
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        errorMessage,
                        color = colors.statusPending,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        // Converte para formato HH:mm para validação
                        val formattedStartTime = formatTimeValue(startTime)
                        val formattedEndTime = formatTimeValue(endTime)

                        errorMessage =
                            when {
                                type.isBlank() -> "Tipo de atividade é obrigatório"
                                area.isBlank() -> "Talhão/Área é obrigatório"
                                startTime.length < 4 -> "Hora de início é obrigatória (4 dígitos)"
                                endTime.length < 4 -> "Hora de término é obrigatória (4 dígitos)"
                                !TimeValidation.isValidTimeFormat(formattedStartTime) -> "Hora de início inválida"
                                !TimeValidation.isValidTimeFormat(formattedEndTime) -> "Hora de término inválida"
                                !TimeValidation.isValidTimeRange(formattedStartTime) -> "Hora de início deve estar entre 00:00 e 23:59"
                                !TimeValidation.isValidTimeRange(formattedEndTime) -> "Hora de término deve estar entre 00:00 e 23:59"
                                !TimeValidation.isEndTimeAfterStartTime(
                                    formattedStartTime,
                                    formattedEndTime,
                                ) -> "Hora de término deve ser após a hora de início"
                                else -> {
                                    // Salva no histórico de atividades
                                    ActivityHistoryManager.addToHistory(context, type.trim())
                                    // Salva no histórico de áreas
                                    AreaHistoryManager.addToHistory(context, area.trim())
                                    // Atualiza a lista de sugestões rápidas
                                    recentActivities = ActivityHistoryManager.getRecentSuggestions(context)

                                    val newTaskRegistry =
                                        TaskRegistry(
                                            type = type.trim(),
                                            area = area.trim(),
                                            startTime = formattedStartTime,
                                            endTime = formattedEndTime,
                                            observations = observations.trim(),
                                        )
                                    onInsertTaskRegistry(newTaskRegistry)
                                    type = ""
                                    area = ""
                                    startTime = ""
                                    endTime = ""
                                    observations = ""
                                    ""
                                }
                            }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .graphicsLayer(scaleX = saveScale, scaleY = saveScale),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = colors.headerBackground,
                            contentColor = Color.White,
                        ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(
                        Icons.Filled.AddCircle,
                        contentDescription = "Salvar",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White,
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        "Salvar Tarefa",
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
fun TaskRegistryItem(taskRegistry: TaskRegistry) {
    val colors = AppTheme.colors
    val isDark = isSystemInDarkTheme()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF424242) else colors.card),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Tipo de tarefa registrada
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = taskRegistry.type,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSystemInDarkTheme()) Color.White else colors.primary,
                    modifier = Modifier.weight(1f),
                )

                // Badge de status de tempo
                val statusColor =
                    when {
                        taskRegistry.startTime.isEmpty() -> colors.textTertiary
                        else -> colors.statusCompleted
                    }

                val (badgeText, badgeBackgroundColor, badgeTextColor) =
                    when {
                        taskRegistry.isDeleted ->
                            Triple(
                                "Excluído",
                                colors.statusPending.copy(alpha = 0.2f),
                                colors.statusPending,
                            )
                        taskRegistry.isModified ->
                            Triple(
                                "Alterado",
                                Color(0xFF90CAF9).copy(alpha = 0.4f), // Azul mais intenso
                                Color(0xFF42A5F5), // Azul mais clarinho para o texto
                            )
                        else ->
                            Triple(
                                "Registrado",
                                statusColor.copy(alpha = 0.2f),
                                statusColor,
                            )
                    }

                Box(
                    modifier =
                        Modifier
                            .background(badgeBackgroundColor, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = badgeTextColor,
                    )
                }
            }

            // Detalhes
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DetailItemWithIcon(
                    label = "Talhão",
                    value = taskRegistry.area,
                    icon = Icons.Filled.Terrain,
                    valueColor = if (isDark) Color.White else null,
                )
                DetailItemWithIcon(
                    label = "Horário",
                    value = "${taskRegistry.startTime} - ${taskRegistry.endTime}",
                    icon = Icons.Filled.AccessTime,
                    valueColor = if (isDark) Color.White else null,
                )
            }

            // Observações
            if (taskRegistry.observations.isNotEmpty()) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(colors.observationsBox, shape = RoundedCornerShape(8.dp))
                            .padding(12.dp),
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Notes,
                                contentDescription = "Observações",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                "Observações",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textSecondary,
                            )
                        }
                        Text(
                            taskRegistry.observations,
                            fontSize = 13.sp,
                            color = colors.textPrimary,
                            lineHeight = 18.sp,
                        )
                    }
                }
            }
        }
    }
}
