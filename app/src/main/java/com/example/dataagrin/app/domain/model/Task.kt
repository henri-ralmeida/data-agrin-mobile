package com.example.dataagrin.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val area: String,
    val scheduledTime: String,
    val status: TaskStatus
)

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}
