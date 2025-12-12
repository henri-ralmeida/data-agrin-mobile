package com.example.dataagrin.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dataagrin.app.domain.model.Activity
import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import com.example.dataagrin.app.domain.usecase.GetActivitiesUseCase
import com.example.dataagrin.app.domain.usecase.InsertActivityUseCase
import com.example.dataagrin.app.domain.usecase.InsertTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ActivityViewModel(
    private val getActivitiesUseCase: GetActivitiesUseCase,
    private val insertActivityUseCase: InsertActivityUseCase,
    private val insertTaskUseCase: InsertTaskUseCase
) : ViewModel() {

    private val _activities = MutableStateFlow<List<Activity>>(emptyList())
    val activities: StateFlow<List<Activity>> = _activities.asStateFlow()

    init {
        loadActivities()
    }

    private fun loadActivities() {
        getActivitiesUseCase().onEach { activityList ->
            _activities.value = activityList
        }.launchIn(viewModelScope)
    }

    fun insertActivity(activity: Activity) {
        viewModelScope.launch {
            // Insere a atividade
            insertActivityUseCase(activity)

            // Cria uma tarefa correspondente automaticamente
            // Usa a hora de início como hora prevista
            val task = Task(
                name = activity.type,
                area = activity.area,
                scheduledTime = activity.startTime,
                status = TaskStatus.PENDING
            )
            insertTaskUseCase(task)
        }
    }
}
