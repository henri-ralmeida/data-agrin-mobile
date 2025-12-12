package com.example.dataagrin.app.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import com.example.dataagrin.app.presentation.viewmodel.TaskViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TaskScreen(viewModel: TaskViewModel = koinViewModel()) {
    val tasks by viewModel.tasks.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Int?>(null) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var editName by remember { mutableStateOf("") }
    var editScheduledTime by remember { mutableStateOf("") }
    var editEndTime by remember { mutableStateOf("") }
    var editArea by remember { mutableStateOf("") }
    var editObservations by remember { mutableStateOf("") }
    var timeError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        TaskScreenHeader()

        // Lista de tarefas
        if (tasks.isEmpty()) {
            EmptyTasksState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks) { task ->
                    TaskCard(
                        task = task,
                        onStatusChange = { newStatus ->
                            viewModel.updateTask(task.copy(status = newStatus))
                        },
                        onEdit = {
                            selectedTask = task
                            editName = task.name
                            editScheduledTime = task.scheduledTime
                            editEndTime = task.endTime
                            editArea = task.area
                            editObservations = task.observations
                            showEditDialog = true
                        },
                        onDelete = {
                            taskToDelete = task.id
                            showDeleteConfirm = true
                        }
                    )
                }
            }
        }

        // Edit Dialog
        if (showEditDialog && selectedTask != null) {
            EditTaskDialog(
                task = selectedTask!!,
                taskName = editName,
                onTaskNameChange = { editName = it },
                scheduledTime = editScheduledTime,
                onScheduledTimeChange = { editScheduledTime = it },
                endTime = editEndTime,
                onEndTimeChange = { editEndTime = it },
                observations = editObservations,
                onObservationsChange = { editObservations = it },
                timeError = timeError,
                onTimeErrorChange = { timeError = it },
                area = editArea,
                onAreaChange = { editArea = it },
                onConfirm = {
                    timeError = ""
                    if (!isValidTimeFormat(editScheduledTime)) {
                        timeError = "Horário de início inválido. Use o formato hh:mm"
                        return@EditTaskDialog
                    }
                    if (editEndTime.isNotEmpty() && !isValidTimeFormat(editEndTime)) {
                        timeError = "Horário de término inválido. Use o formato hh:mm"
                        return@EditTaskDialog
                    }
                    selectedTask?.let { task ->
                        viewModel.updateTask(
                            task.copy(
                                name = editName,
                                scheduledTime = editScheduledTime,
                                endTime = editEndTime,
                                area = editArea,
                                observations = editObservations
                            )
                        )
                    }
                    showEditDialog = false
                    selectedTask = null
                },
                onDismiss = {
                    showEditDialog = false
                    selectedTask = null
                    timeError = ""
                }
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirm && taskToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteConfirm = false
                    taskToDelete = null
                },
                title = {
                    Text(
                        "Confirmar exclusão",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Text(
                        "Tem certeza que deseja deletar esta tarefa? Esta ação não pode ser desfeita.",
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                showDeleteConfirm = false
                                taskToDelete = null
                            },
                            modifier = Modifier
                                .width(120.dp)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray
                            )
                        ) {
                            Text("Cancelar", color = Color.Black, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                taskToDelete?.let { viewModel.deleteTask(it) }
                                showDeleteConfirm = false
                                taskToDelete = null
                            },
                            modifier = Modifier
                                .width(120.dp)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            )
                        ) {
                            Text("Deletar", fontSize = 14.sp)
                        }
                    }
                },
                dismissButton = {}
            )
        }
    }
}

@Composable
private fun TaskScreenHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B5E20))
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Tarefas do Dia",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Gerencie suas atividades agrícolas",
                fontSize = 14.sp,
                color = Color(0xFFE8F5E9),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Emoji decorativo de trator
        Text(
            "🚜",
            fontSize = 120.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .rotate(15f)
                .alpha(0.5f)
        )
    }
}

@Composable
private fun EmptyTasksState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = "Sem tarefas",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF1B5E20)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Nenhuma tarefa para hoje",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Novas tarefas aparecerão aqui",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E)
            )
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onStatusChange: (TaskStatus) -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = false) { },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Linha 1: Nome da tarefa
            Text(
                text = task.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Linha 2: Talhão + Horário
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItemWithIcon(
                    label = "Talhão",
                    value = task.area,
                    icon = Icons.Filled.Terrain
                )
                DetailItemWithIcon(
                    label = "Horário",
                    value = if (task.endTime.isNotEmpty()) "${task.scheduledTime} - ${task.endTime}" else task.scheduledTime,
                    icon = Icons.Filled.AccessTime
                )
            }

            // Linha 2.5: Observações (se houver)
            if (task.observations.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                        .padding(bottom = 12.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notes,
                                contentDescription = "Observações",
                                tint = Color(0xFF666666),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Observações",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF666666)
                            )
                        }
                        Text(
                            task.observations,
                            fontSize = 13.sp,
                            color = Color(0xFF333333),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Linha 3: Status + Botão atualizar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(task.status)
                
                // Botão para ciclar status
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF1B5E20),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            val nextStatus = when (task.status) {
                                TaskStatus.PENDING -> TaskStatus.IN_PROGRESS
                                TaskStatus.IN_PROGRESS -> TaskStatus.COMPLETED
                                TaskStatus.COMPLETED -> TaskStatus.PENDING
                            }
                            onStatusChange(nextStatus)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Atualizar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            // Linha 4: Edit e Delete buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Edit button
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar tarefa",
                        tint = Color(0xFF1B5E20)
                    )
                }

                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Deletar tarefa",
                        tint = Color.Red
                    )
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

@Composable
private fun DetailItemWithIcon(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF1B5E20),
            modifier = Modifier.size(18.dp)
        )
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
}

@Composable
private fun StatusBadge(status: TaskStatus) {
    data class StatusInfo(
        val backgroundColor: Color,
        val textColor: Color,
        val label: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector
    )

    val statusInfo = when (status) {
        TaskStatus.PENDING -> StatusInfo(
            Color(0xFFD32F2F),
            Color.White,
            "Pendente",
            Icons.Filled.Schedule
        )
        TaskStatus.IN_PROGRESS -> StatusInfo(
            Color(0xFFF57C00),
            Color.White,
            "Em andamento",
            Icons.Filled.PlayCircle
        )
        TaskStatus.COMPLETED -> StatusInfo(
            Color(0xFF1B5E20),
            Color.White,
            "Finalizada",
            Icons.Filled.CheckCircleOutline
        )
    }

    Box(
        modifier = Modifier
            .background(statusInfo.backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = statusInfo.icon,
                contentDescription = statusInfo.label,
                tint = statusInfo.textColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                statusInfo.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = statusInfo.textColor
            )
        }
    }
}

fun TaskStatus.displayName(): String = when (this) {
    TaskStatus.PENDING -> "Pendente"
    TaskStatus.IN_PROGRESS -> "Em andamento"
    TaskStatus.COMPLETED -> "Finalizada"
}

@Composable
private fun EditTaskDialog(
    task: Task,
    taskName: String,
    onTaskNameChange: (String) -> Unit,
    scheduledTime: String,
    onScheduledTimeChange: (String) -> Unit,
    endTime: String,
    onEndTimeChange: (String) -> Unit,
    observations: String,
    onObservationsChange: (String) -> Unit,
    timeError: String = "",
    onTimeErrorChange: (String) -> Unit = {},
    area: String,
    onAreaChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Editar Tarefa",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Task Name Field
                OutlinedTextField(
                    value = taskName,
                    onValueChange = onTaskNameChange,
                    label = { Text("Nome da Tarefa") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        focusedLabelColor = Color(0xFF1B5E20)
                    )
                )

                // Scheduled Time Field
                Column {
                    OutlinedTextField(
                        value = scheduledTime,
                        onValueChange = {
                            onScheduledTimeChange(it)
                            onTimeErrorChange("")
                        },
                        label = { Text("Horário de Início") },
                        placeholder = { Text("hh:mm") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = timeError.isNotEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (timeError.isNotEmpty()) Color.Red else Color(0xFF1B5E20),
                            focusedLabelColor = Color(0xFF1B5E20),
                            errorBorderColor = Color.Red,
                            errorLabelColor = Color.Red
                        )
                    )
                    if (timeError.isNotEmpty()) {
                        Text(
                            timeError,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                        )
                    }
                }

                // End Time Field
                OutlinedTextField(
                    value = endTime,
                    onValueChange = {
                        onEndTimeChange(it)
                        onTimeErrorChange("")
                    },
                    label = { Text("Horário de Término") },
                    placeholder = { Text("hh:mm (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        focusedLabelColor = Color(0xFF1B5E20)
                    )
                )

                // Area Field
                OutlinedTextField(
                    value = area,
                    onValueChange = onAreaChange,
                    label = { Text("Talhão") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        focusedLabelColor = Color(0xFF1B5E20)
                    )
                )

                // Observations Field
                OutlinedTextField(
                    value = observations,
                    onValueChange = onObservationsChange,
                    label = { Text("Observações") },
                    placeholder = { Text("Adicione observações... (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        focusedLabelColor = Color(0xFF1B5E20)
                    )
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .width(120.dp)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray
                    )
                ) {
                    Text("Cancelar", color = Color.Black, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .width(120.dp)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B5E20)
                    )
                ) {
                    Text("Salvar", fontSize = 13.sp)
                }
            }
        },
        dismissButton = {}
    )
}

private fun isValidTimeFormat(time: String): Boolean {
    if (time.isEmpty()) return false
    val regex = Regex("^([0-1][0-9]|2[0-3]):[0-5][0-9]$")
    return regex.matches(time)
}