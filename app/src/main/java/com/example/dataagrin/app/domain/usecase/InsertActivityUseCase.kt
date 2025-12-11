package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.Activity
import com.example.dataagrin.app.domain.repository.ActivityRepository

class InsertActivityUseCase(private val activityRepository: ActivityRepository) {
    suspend operator fun invoke(activity: Activity) {
        activityRepository.insertActivity(activity)
    }
}
