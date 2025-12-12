package com.example.dataagrin.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dataagrin.app.domain.model.TaskRegistry
import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import com.example.dataagrin.app.domain.usecase.GetTaskRegistriesUseCase
import com.example.dataagrin.app.domain.usecase.InsertTaskRegistryUseCase
import com.example.dataagrin.app.domain.usecase.InsertTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TaskRegistryViewModel(
    private val getTaskRegistriesUseCase: GetTaskRegistriesUseCase,
    private val insertTaskRegistryUseCase: InsertTaskRegistryUseCase,
    private val insertTaskUseCase: InsertTaskUseCase,
    private val taskViewModel: TaskViewModel
) : ViewModel() {

    private val _taskRegistries = MutableStateFlow<List<TaskRegistry>>(emptyList())
    val taskRegistries: StateFlow<List<TaskRegistry>> = _taskRegistries.asStateFlow()

    init {
        loadTaskRegistries()
    }

    private fun loadTaskRegistries() {
        getTaskRegistriesUseCase().onEach { registryList ->
            _taskRegistries.value = registryList
        }.launchIn(viewModelScope)
    }

    fun insertTaskRegistry(taskRegistry: TaskRegistry) {
        viewModelScope.launch {
            // Insere o registro de tarefa
            insertTaskRegistryUseCase(taskRegistry)

            // Cria uma tarefa correspondente automaticamente
            // Usa a hora de início como hora prevista
            val task = Task(
                name = taskRegistry.type,
                area = taskRegistry.area,
                scheduledTime = taskRegistry.startTime,
                endTime = taskRegistry.endTime,
                observations = taskRegistry.observations,
                status = TaskStatus.PENDING
            )
            
            // Insere no banco local e obtém o ID gerado pelo Room
            val generatedId = insertTaskUseCase(task)
            
            // Cria task com o ID correto gerado pelo Room para sincronizar com Firebase
            val taskWithCorrectId = task.copy(id = generatedId.toInt())
            
            // Sincroniza com Firebase usando o ID correto
            taskViewModel.createTask(taskWithCorrectId)
        }
    }
}

