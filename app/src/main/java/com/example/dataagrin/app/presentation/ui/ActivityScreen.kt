package com.example.dataagrin.app.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun ActivityScreen(windowSizeClass: WindowSizeClass, viewModel: ActivityViewModel = koinViewModel()) {
    val activities by viewModel.activities.collectAsState()

    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            CompactActivityScreen(activities = activities, onInsertActivity = viewModel::insertActivity)
        }
        else -> {
            ExpandedActivityScreen(activities = activities, onInsertActivity = viewModel::insertActivity)
        }
    }
}

@Composable
fun CompactActivityScreen(activities: List<Activity>, onInsertActivity: (Activity) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ActivityForm(onInsertActivity)
        Spacer(modifier = Modifier.height(16.dp))
        ActivityHistory(activities, modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun ExpandedActivityScreen(activities: List<Activity>, onInsertActivity: (Activity) -> Unit) {
    Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            ActivityForm(onInsertActivity)
        }
        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            ActivityHistory(activities, modifier = Modifier.fillMaxSize())
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

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Registrar Atividade", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            label = { Text("Tipo de Atividade") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ex: Plantio, Pulverização") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = area,
            onValueChange = { area = it },
            label = { Text("Talhão/Área") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ex: Talhão 10") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = startTime,
            onValueChange = { startTime = it },
            label = { Text("Hora de Início") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("HH:mm") },
            readOnly = true,
            trailingIcon = { Text("🕐") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = endTime,
            onValueChange = { endTime = it },
            label = { Text("Hora de Término") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("HH:mm") },
            readOnly = true,
            trailingIcon = { Text("🕐") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = observations,
            onValueChange = { observations = it },
            label = { Text("Observações") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            placeholder = { Text("Digite suas observações...") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        Button(
            onClick = {
                errorMessage = when {
                    type.isBlank() -> "Tipo de atividade é obrigatório"
                    area.isBlank() -> "Talhão/Área é obrigatório"
                    startTime.isBlank() -> "Hora de início é obrigatória"
                    endTime.isBlank() -> "Hora de término é obrigatória"
                    else -> {
                        val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Atividade")
        }
    }
}

@Composable
fun ActivityHistory(activities: List<Activity>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text("Histórico de Atividades")
        LazyColumn {
            items(activities, key = { it.id }) { activity ->
                ActivityItem(activity = activity)
            }
        }
    }
}

@Composable
fun ActivityItem(activity: Activity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Tipo: ${activity.type}", fontWeight = FontWeight.SemiBold)
            Text(text = "Área: ${activity.area}")
            Text(text = "Horário: ${activity.startTime} - ${activity.endTime}", fontSize = 12.sp)
            if (activity.observations.isNotEmpty()) {
                Text(text = "Obs: ${activity.observations}", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}