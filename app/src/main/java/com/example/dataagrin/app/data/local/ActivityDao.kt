package com.example.dataagrin.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dataagrin.app.domain.model.TaskRegistry
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskRegistryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskRegistry(taskRegistry: TaskRegistry)

    @Query("SELECT * FROM activities ORDER BY id DESC")
    fun getAllTaskRegistries(): Flow<List<TaskRegistry>>
}
