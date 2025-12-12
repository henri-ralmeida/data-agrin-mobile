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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dataagrin.app.domain.model.Activity
import com.example.dataagrin.app.presentation.viewmodel.ActivityViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActivityScreen(viewModel: ActivityViewModel = koinViewModel()) {
    val activities by viewModel.activities.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        ActivityScreenHeader()

        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ActivityForm(onInsertActivity = viewModel::insertActivity)
            }

            item {
                ActivityHistoryHeader(count = activities.size)
            }

            items(activities, key = { it.id }) { activity ->
                ActivityItem(activity = activity)
            }
        }
    }
}

@Composable
private fun ActivityScreenHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B5E20))
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Registros de Atividades",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Registre e acompanhe todas as atividades",
                fontSize = 14.sp,
                color = Color(0xFFE8F5E9),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun ActivityHistoryHeader(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Histórico de Atividades",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                "$count atividade${if (count != 1) "s" else ""} registrada${if (count != 1) "s" else ""}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ActivityForm(onInsertActivity: (Activity) -> Unit) {
    var type by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header da forma
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Adicionar atividade",
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF1B5E20)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        "Registrar Atividade",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Filled.ExpandLess,
                        contentDescription = "Expandir/Recolher",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(20.dp)
                            .let {
                                if (!isExpanded) it
                                else it
                            }
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // Formulário
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Tipo de Atividade") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ex: Plantio, Pulverização") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        focusedLabelColor = Color(0xFF1B5E20)
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Talhão/Área") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ex: Talhão 10") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        focusedLabelColor = Color(0xFF1B5E20)
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Início") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("hh:mm") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1B5E20),
                            focusedLabelColor = Color(0xFF1B5E20)
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("Término") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("hh:mm") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1B5E20),
                            focusedLabelColor = Color(0xFF1B5E20)
                        ),
                        singleLine = true
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
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        focusedLabelColor = Color(0xFF1B5E20)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        errorMessage,
                        color = Color(0xFFF44336),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        errorMessage = when {
                            type.isBlank() -> "Tipo de atividade é obrigatório"
                            area.isBlank() -> "Talhão/Área é obrigatório"
                            startTime.isBlank() -> "Hora de início é obrigatória"
                            endTime.isBlank() -> "Hora de término é obrigatória"
                            else -> {
                                val newActivity = Activity(
                                    type = type.trim(),
                                    area = area.trim(),
                                    startTime = startTime,
                                    endTime = endTime,
                                    observations = observations.trim()
                                )
                                onInsertActivity(newActivity)
                                type = ""
                                area = ""
                                startTime = ""
                                endTime = ""
                                observations = ""
                                ""
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B5E20),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Salvar",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        "Salvar Atividade",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityItem(activity: Activity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Tipo de atividade
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = activity.type,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
                    modifier = Modifier.weight(1f)
                )
                
                // Badge de status de tempo
                val statusColor = when {
                    activity.startTime.isEmpty() -> Color(0xFF9E9E9E)
                    else -> Color(0xFF4CAF50)
                }
                
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Registrado",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor
                    )
                }
            }

            // Detalhes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItem(label = "Talhão", value = activity.area)
                DetailItem(label = "Horário", value = "${activity.startTime} - ${activity.endTime}")
            }

            // Observações
            if (activity.observations.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            "Observações",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            activity.observations,
                            fontSize = 13.sp,
                            color = Color(0xFF333333),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF9E9E9E)
        )
        Text(
            value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}