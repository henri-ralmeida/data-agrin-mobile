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
    val status: TaskStatus,
    // BaaS Sync Fields (para Firebase/Supabase)
    val remoteId: String? = null,
    val syncStatus: SyncStatus = SyncStatus.LOCAL,
    val lastSyncedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}

enum class SyncStatus {
    LOCAL,        // Apenas local, não sincronizado
    SYNCING,      // Em processo de sincronização
    SYNCED,       // Sincronizado com o servidor
    SYNC_ERROR    // Erro na sincronização
}
