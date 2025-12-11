package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.Activity
import com.example.dataagrin.app.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow

class GetActivitiesUseCase(private val activityRepository: ActivityRepository) {
    operator fun invoke(): Flow<List<Activity>> {
        return activityRepository.getAllActivities()
    }
}
