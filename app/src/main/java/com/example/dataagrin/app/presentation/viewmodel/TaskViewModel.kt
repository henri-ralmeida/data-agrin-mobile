package com.example.dataagrin.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dataagrin.app.domain.model.SyncStatus
import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskRegistry
import com.example.dataagrin.app.domain.usecase.DeleteTaskUseCase
import com.example.dataagrin.app.domain.usecase.GetTasksUseCase
import com.example.dataagrin.app.domain.usecase.InsertTaskRegistryUseCase
import com.example.dataagrin.app.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TaskViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val insertTaskRegistryUseCase: InsertTaskRegistryUseCase
    // TODO: Injetar SyncRepository quando implementar BaaS
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        getTasksUseCase().onEach { taskList ->
            _tasks.value = taskList
        }.launchIn(viewModelScope)
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            // Marca como LOCAL primeiro (indica que precisa sincronizar)
            val updatedTask = task.copy(
                syncStatus = SyncStatus.LOCAL,
                updatedAt = System.currentTimeMillis()
            )
            updateTaskUseCase(updatedTask)

            // Cria um registro no histórico de tarefas como alteração
            val taskRegistry = TaskRegistry(
                type = updatedTask.name,
                area = updatedTask.area,
                startTime = updatedTask.scheduledTime,
                endTime = updatedTask.endTime,
                observations = updatedTask.observations,
                isModified = true
            )
            insertTaskRegistryUseCase(taskRegistry)

            // TODO: Quando implementar BaaS, fazer sync aqui
            // trySync(updatedTask)
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            deleteTaskUseCase(taskId)
        }
    }

    // Placeholder para sincronização com Firebase/Supabase
    // fun trySync(task: Task) {
    //     viewModelScope.launch {
    //         try {
    //             _syncState.value = SyncStatus.SYNCING
    //             // syncRepository.syncTask(task)
    //             _syncState.value = SyncStatus.SYNCED
    //         } catch (e: Exception) {
    //             _syncState.value = SyncStatus.SYNC_ERROR
    //         }
    //     }
    // }
}
