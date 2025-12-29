package com.example.dataagrin.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dataagrin.app.data.firebase.TaskFirestoreRepository
import com.example.dataagrin.app.domain.model.SyncStatus
import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskRegistry
import com.example.dataagrin.app.domain.usecase.DeleteTaskUseCase
import com.example.dataagrin.app.domain.usecase.GetTaskByIdUseCase
import com.example.dataagrin.app.domain.usecase.GetTasksUseCase
import com.example.dataagrin.app.domain.usecase.InsertTaskRegistryUseCase
import com.example.dataagrin.app.domain.usecase.InsertTaskUseCase
import com.example.dataagrin.app.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TaskViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val insertTaskUseCase: InsertTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val taskFirestoreRepository: TaskFirestoreRepository,
    private val insertTaskRegistryUseCase: InsertTaskRegistryUseCase,
) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _syncStatus = MutableStateFlow(SyncStatus.LOCAL)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        getTasksUseCase()
            .onEach { taskList ->
                _tasks.value = taskList
            }.launchIn(viewModelScope)
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            // Busca a task original para comparar
            val originalTask = getTaskByIdUseCase(task.id)

            // Detecta o que foi alterado
            val changes = mutableListOf<String>()
            originalTask?.let { original ->
                if (original.name != task.name) changes.add("Nome da Tarefa")
                if (original.area != task.area) changes.add("Talhão")
                if (original.scheduledTime != task.scheduledTime || original.endTime != task.endTime) changes.add("Horário")
                if (original.observations != task.observations) changes.add("Observação")
                if (original.status != task.status) changes.add("Status")
            }

            val observationText =
                if (changes.isNotEmpty()) {
                    "Alteração: ${changes.joinToString(", ")}"
                } else {
                    "Tarefa alterada"
                }

            // Marca como SYNCING
            val updatedTask =
                task.copy(
                    syncStatus = SyncStatus.SYNCING,
                    updatedAt = System.currentTimeMillis(),
                )
            updateTaskUseCase(updatedTask)

            // Cria registro no histórico de tarefas (com flag isModified)
            // Usa o horário da tarefa (`scheduledTime`/`endTime`) em vez da hora do dispositivo
            val taskRegistry =
                TaskRegistry(
                    type = task.name,
                    area = task.area,
                    startTime = task.scheduledTime,
                    endTime = task.endTime.ifEmpty { task.scheduledTime },
                    observations = observationText,
                    isModified = true,
                    isDeleted = false,
                )
            insertTaskRegistryUseCase(taskRegistry)

            // Sincroniza com Firebase (com ação alterado)
            // Histórico é automaticamente criado em /tasks/{id}/history/
            trySyncUpdate(updatedTask)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            // Cria registro no histórico de tarefas (com flag isDeleted)
            // Usa o horário da tarefa para manter consistência com o que foi agendado
            val taskRegistry =
                TaskRegistry(
                    type = task.name,
                    area = task.area,
                    startTime = task.scheduledTime,
                    endTime = task.endTime.ifEmpty { task.scheduledTime },
                    observations = "Tarefa excluída",
                    isModified = false,
                    isDeleted = true,
                )
            insertTaskRegistryUseCase(taskRegistry)

            deleteTaskUseCase(task.id)

            // Sincroniza com Firebase (com ação DELETADO)
            // Histórico é automaticamente criado em /tasks/{id}/history/
            trySyncDelete(task)
        }
    }

    fun createTask(task: Task) {
        viewModelScope.launch {
            try {
                // 1. Obter pr\u00f3ximo ID sequencial do Firebase
                val nextId = taskFirestoreRepository.getNextTaskId()

                // 2. Criar task com o ID correto
                val newTask =
                    task.copy(
                        id = nextId,
                        syncStatus = SyncStatus.SYNCING,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis(),
                    )

                // 3. Insere no Room com o ID do Firebase
                insertTaskUseCase(newTask)

                // 4. Sincroniza com Firebase (cria tasks[nextId])
                trySyncTask(newTask)
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.SYNC_ERROR
            }
        }
    }

    private fun trySyncTask(task: Task) {
        viewModelScope.launch {
            try {
                _syncStatus.value = SyncStatus.SYNCING
                taskFirestoreRepository.uploadTask(task)
                _syncStatus.value = SyncStatus.SYNCED
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.SYNC_ERROR
            }
        }
    }

    private fun trySyncUpdate(task: Task) {
        viewModelScope.launch {
            try {
                _syncStatus.value = SyncStatus.SYNCING
                taskFirestoreRepository.updateTask(task)
                _syncStatus.value = SyncStatus.SYNCED
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.SYNC_ERROR
            }
        }
    }

    private fun trySyncDelete(task: Task) {
        viewModelScope.launch {
            try {
                _syncStatus.value = SyncStatus.SYNCING
                taskFirestoreRepository.deleteTask(task)
                _syncStatus.value = SyncStatus.SYNCED
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.SYNC_ERROR
            }
        }
    }
}
