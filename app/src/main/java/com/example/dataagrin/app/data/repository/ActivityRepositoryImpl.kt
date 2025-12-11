package com.example.dataagrin.app.data.repository

import com.example.dataagrin.app.data.local.ActivityDao
import com.example.dataagrin.app.domain.model.Activity
import com.example.dataagrin.app.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow

class ActivityRepositoryImpl(private val activityDao: ActivityDao) : ActivityRepository {
    override fun getAllActivities(): Flow<List<Activity>> {
        return activityDao.getAllActivities()
    }

    override suspend fun insertActivity(activity: Activity) {
        activityDao.insertActivity(activity)
    }
}
