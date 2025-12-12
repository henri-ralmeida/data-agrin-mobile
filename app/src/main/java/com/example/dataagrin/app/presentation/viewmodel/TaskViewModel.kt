package com.example.dataagrin.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dataagrin.app.data.firebase.TaskFirestoreRepository
import com.example.dataagrin.app.domain.model.SyncStatus
import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.usecase.DeleteTaskUseCase
import com.example.dataagrin.app.domain.usecase.GetTasksUseCase
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
    private val insertTaskUseCase: InsertTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val taskFirestoreRepository: TaskFirestoreRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.LOCAL)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

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
            // Marca como SYNCING
            val updatedTask = task.copy(
                syncStatus = SyncStatus.SYNCING,
                updatedAt = System.currentTimeMillis()
            )
            updateTaskUseCase(updatedTask)

            // Sincroniza com Firebase (com ação alterado)
            // Histórico é automaticamente criado em /tasks/{id}/history/
            trySyncUpdate(updatedTask)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            deleteTaskUseCase(task.id)

            // Sincroniza com Firebase (com ação DELETADO)
            // Histórico é automaticamente criado em /tasks/{id}/history/
            trySyncDelete(task.id)
        }
    }

    fun createTask(task: Task) {
        viewModelScope.launch {
            try {
                // 1. Obter pr\u00f3ximo ID sequencial do Firebase
                val nextId = taskFirestoreRepository.getNextTaskId()
                
                // 2. Criar task com o ID correto
                val newTask = task.copy(
                    id = nextId,
                    syncStatus = SyncStatus.SYNCING,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                
                // 3. Insere no Room com o ID do Firebase
                insertTaskUseCase(newTask)
                
                // 4. Sincroniza com Firebase (cria tasks[nextId])
                trySyncTask(newTask)
                
            } catch (e: Exception) {
                e.printStackTrace()
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
                e.printStackTrace()
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
                e.printStackTrace()
            }
        }
    }

    private fun trySyncDelete(taskId: Int) {
        viewModelScope.launch {
            try {
                _syncStatus.value = SyncStatus.SYNCING
                taskFirestoreRepository.deleteTask(taskId)
                _syncStatus.value = SyncStatus.SYNCED
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.SYNC_ERROR
                e.printStackTrace()
            }
        }
    }

    fun syncAllTasks() {
        viewModelScope.launch {
            try {
                _syncStatus.value = SyncStatus.SYNCING
                
                for (task in _tasks.value) {
                    taskFirestoreRepository.updateTask(task)
                }
                
                _syncStatus.value = SyncStatus.SYNCED
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.SYNC_ERROR
                e.printStackTrace()
            }
        }
    }
}
