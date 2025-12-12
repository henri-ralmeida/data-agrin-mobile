package com.example.dataagrin.app.data.firebase

import com.example.dataagrin.app.domain.model.TaskRegistry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TaskRegistryFirestoreRepository(private val firestore: FirebaseFirestore) {

    /**
     * Salva registro de atividade na coleção principal
     * Útil para ter um histórico global de todas as ações
     */
    suspend fun uploadTaskRegistry(registry: TaskRegistry) {
        try {
            firestore
                .collection(FirebaseManager.TASK_REGISTRY_COLLECTION)
                .document(registry.id.toString())
                .set(registry.toFirestoreMap())
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getAllTaskRegistries(): List<TaskRegistry> {
        return try {
            val snapshot = firestore
                .collection(FirebaseManager.TASK_REGISTRY_COLLECTION)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.data?.toTaskRegistry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTaskRegistriesByTaskId(taskId: Int): List<TaskRegistry> {
        return try {
            val snapshot = firestore
                .collection(FirebaseManager.TASK_REGISTRY_COLLECTION)
                .whereEqualTo("taskId", taskId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.data?.toTaskRegistry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Busca histórico de uma tarefa específica (via sub-collection)
     * Estrutura: /tasks/{taskId}/history/{historyId}
     */
    suspend fun getTaskHistoryFromSubcollection(taskId: Int): List<Map<String, Any>> {
        return try {
            val snapshot = firestore
                .collection(FirebaseManager.TASKS_COLLECTION)
                .document(taskId.toString())
                .collection("history")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.data
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
