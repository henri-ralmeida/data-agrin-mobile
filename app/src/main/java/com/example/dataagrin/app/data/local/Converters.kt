package com.example.dataagrin.app.data.local

import androidx.room.TypeConverter
import com.example.dataagrin.app.domain.model.TaskStatus

class Converters {
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toTaskStatus(status: String): TaskStatus {
        return TaskStatus.valueOf(status)
    }
}
