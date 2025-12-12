package com.example.dataagrin.app.data.firebase

import com.example.dataagrin.app.domain.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TaskFirestoreRepository(private val firestore: FirebaseFirestore) {

    /**
     * Salva tarefa e registra ação no histórico
     * Estrutura: /tasks/{taskId}/data e /tasks/{taskId}/history/{historyId}
     */
    suspend fun uploadTask(task: Task, action: String = "registrado") {
        try {
            val taskId = task.id.toString()
            
            // Salva dados da tarefa em /tasks/{taskId}
            firestore
                .collection(FirebaseManager.TASKS_COLLECTION)
                .document(taskId)
                .set(task.toFirestoreMap())
                .await()
            
            // Registra ação no histórico em /tasks/{taskId}/history/{historyId}
            val currentHour = java.util.Calendar.getInstance().let { "${it.get(java.util.Calendar.HOUR_OF_DAY)}:${String.format("%02d", it.get(java.util.Calendar.MINUTE))}" }
            val historyEntry = mapOf(
                "action" to action,
                "timestamp" to System.currentTimeMillis(),
                "formattedTime" to currentHour,
                "taskName" to task.name,
                "area" to task.area
            )
            
            // Obter próximo ID incremental para o histórico
            val historySnapshot = firestore
                .collection(FirebaseManager.TASKS_COLLECTION)
                .document(taskId)
                .collection("history")
                .get()
                .await()
            val nextHistoryId = historySnapshot.documents.size.toString()
            
            firestore
                .collection(FirebaseManager.TASKS_COLLECTION)
                .document(taskId)
                .collection("history")
                .document(nextHistoryId)
                .set(historyEntry)
                .await()
                
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteTask(taskId: Int) {
        try {
            val taskIdStr = taskId.toString()
            
            // Registra exclusão no histórico antes de deletar
            val historyEntry = mapOf(
                "action" to "excluido",
                "timestamp" to System.currentTimeMillis()
            )
            
            // Obter próximo ID incremental para o histórico
            val historySnapshot = firestore
                .collection(FirebaseManager.TASKS_COLLECTION)
                .document(taskIdStr)
                .collection("history")
                .get()
                .await()
            val nextHistoryId = historySnapshot.documents.size.toString()
            
            firestore
                .collection(FirebaseManager.TASKS_COLLECTION)
                .document(taskIdStr)
                .collection("history")
                .document(nextHistoryId)
                .set(historyEntry)
                .await()
            
            // Deleta a tarefa (Firestore deleta automático também as sub-collections em 30 dias)
            // Para deletar imediatamente, descomentar:
            // firestore.collection(FirebaseManager.TASKS_COLLECTION).document(taskIdStr).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getAllTasks(): List<Task> {
        return try {
            val snapshot = firestore
                .collection(FirebaseManager.TASKS_COLLECTION)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.data?.toTask()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTaskById(taskId: Int): Task? {
        return try {
            val snapshot = firestore
                .collection(FirebaseManager.TASKS_COLLECTION)
                .document(taskId.toString())
                .get()
                .await()

            snapshot.data?.toTask()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Atualiza tarefa e registra ação "alterado" no histórico
     */
    suspend fun updateTask(task: Task) {
        uploadTask(task, "alterado")
    }

    /**
     * Busca o próximo ID sequencial para tasks (começando do 0)
     */
    suspend fun getNextTaskId(): Int {
        return try {
            val snapshot = firestore
                .collection(FirebaseManager.TASKS_COLLECTION)
                .get()
                .await()
            
            // Retorna o número de documentos (próximo ID será count)
            snapshot.documents.size
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * Busca histórico completo de uma tarefa
     */
    suspend fun getTaskHistory(taskId: Int): List<Map<String, Any>> {
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
