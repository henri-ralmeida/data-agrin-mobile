package com.example.dataagrin.app.data.firebase

import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskRegistry
import com.example.dataagrin.app.domain.model.TaskStatus

// Extensão para converter Task para Map (Firestore)
fun Task.toFirestoreMap(): Map<String, Any> {
    return mapOf(
        "id" to this.id,
        "name" to this.name,
        "area" to this.area,
        "scheduledTime" to this.scheduledTime,
        "endTime" to this.endTime,
        "observations" to this.observations,
        "status" to this.status.name,
        "remoteId" to (this.remoteId ?: ""),
        "syncStatus" to this.syncStatus.name,
        "lastSyncedAt" to (this.lastSyncedAt ?: 0L),
        "createdAt" to this.createdAt,
        "updatedAt" to this.updatedAt
    )
}

// Extensão para converter Map do Firestore para Task
fun Map<String, Any>.toTask(): Task {
    return Task(
        id = (this["id"] as? Number)?.toInt() ?: 0,
        name = this["name"] as? String ?: "",
        area = this["area"] as? String ?: "",
        scheduledTime = this["scheduledTime"] as? String ?: "",
        endTime = this["endTime"] as? String ?: "",
        observations = this["observations"] as? String ?: "",
        status = try {
            TaskStatus.valueOf(this["status"] as? String ?: "PENDING")
        } catch (e: Exception) {
            TaskStatus.PENDING
        },
        remoteId = this["remoteId"] as? String,
        createdAt = (this["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
        updatedAt = (this["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
    )
}

// Extensão para converter TaskRegistry para Map (Firestore)
fun TaskRegistry.toFirestoreMap(): Map<String, Any> {
    return mapOf(
        "id" to this.id,
        "type" to this.type,
        "area" to this.area,
        "startTime" to this.startTime,
        "endTime" to this.endTime,
        "observations" to this.observations,
        "isModified" to this.isModified,
        "isDeleted" to this.isDeleted
    )
}

// Extensão para converter Map do Firestore para TaskRegistry
fun Map<String, Any>.toTaskRegistry(): TaskRegistry {
    return TaskRegistry(
        id = (this["id"] as? Number)?.toInt() ?: 0,
        type = this["type"] as? String ?: "",
        area = this["area"] as? String ?: "",
        startTime = this["startTime"] as? String ?: "",
        endTime = this["endTime"] as? String ?: "",
        observations = this["observations"] as? String ?: "",
        isModified = this["isModified"] as? Boolean ?: false,
        isDeleted = this["isDeleted"] as? Boolean ?: false
    )
}
