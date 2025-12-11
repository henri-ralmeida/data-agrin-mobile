package com.example.dataagrin.app.domain.model

data class Task(
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
