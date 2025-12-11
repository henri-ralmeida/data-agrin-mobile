package com.example.dataagrin.app.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import com.example.dataagrin.app.presentation.viewmodel.TaskViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TaskScreen(viewModel: TaskViewModel = koinViewModel()) {
    val tasks by viewModel.tasks.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(tasks) { task ->
            TaskItem(task = task, onStatusChange = viewModel::updateTask)
        }
    }
}

@Composable
fun TaskItem(task: Task, onStatusChange: (Task) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Atividade: ${task.name}")
            Text(text = "Área: ${task.area}")
            Text(text = "Hora: ${task.scheduledTime}")
            Text(text = "Status: ${task.status.name}")

            Row(modifier = Modifier.padding(top = 8.dp)) {
                val nextStatus = when (task.status) {
                    TaskStatus.PENDING -> TaskStatus.IN_PROGRESS
                    TaskStatus.IN_PROGRESS -> TaskStatus.COMPLETED
                    TaskStatus.COMPLETED -> TaskStatus.PENDING
                }
                Button(onClick = { onStatusChange(task.copy(status = nextStatus)) }) {
                    Text(text = "Mudar para ${nextStatus.name}")
                }
            }
        }
    }
}