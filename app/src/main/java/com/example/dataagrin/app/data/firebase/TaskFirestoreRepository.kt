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
                "area" to task.area,
                "scheduledTime" to task.scheduledTime,
                "endTime" to task.endTime,
                "status" to task.status.name
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

    suspend fun deleteTask(task: Task) {
        try {
            val taskIdStr = task.id.toString()
            
            // Obtém hora formatada atual
            val currentHour = java.util.Calendar.getInstance().let { 
                "${it.get(java.util.Calendar.HOUR_OF_DAY)}:${String.format("%02d", it.get(java.util.Calendar.MINUTE))}" 
            }
            
            // Registra exclusão no histórico antes de deletar
            val historyEntry = mapOf(
                "action" to "excluido",
                "timestamp" to System.currentTimeMillis(),
                "formattedTime" to currentHour,
                "taskName" to task.name,
                "area" to task.area,
                "scheduledTime" to task.scheduledTime,
                "endTime" to task.endTime,
                "status" to task.status.name
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

    /**
     * Atualiza tarefa e registra ação "alterado" no histórico com todos os campos
     */
    suspend fun updateTask(task: Task) {
        try {
            val taskId = task.id.toString()
            
            // Atualiza dados da tarefa em /tasks/{taskId}
            firestore
                .collection(FirebaseManager.TASKS_COLLECTION)
                .document(taskId)
                .set(task.toFirestoreMap())
                .await()
            
            // Registra ação no histórico em /tasks/{taskId}/history/{historyId}
            val currentHour = java.util.Calendar.getInstance().let { 
                "${it.get(java.util.Calendar.HOUR_OF_DAY)}:${String.format("%02d", it.get(java.util.Calendar.MINUTE))}" 
            }
            val historyEntry = mapOf(
                "action" to "alterado",
                "timestamp" to System.currentTimeMillis(),
                "formattedTime" to currentHour,
                "taskName" to task.name,
                "area" to task.area,
                "scheduledTime" to task.scheduledTime,
                "endTime" to task.endTime,
                "status" to task.status.name
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

    /**
     * Busca o próximo ID sequencial para tasks
     * Se Firebase estiver vazio, começa do 1
     * Se tiver dados, retorna (maior ID existente + 1)
     */
    suspend fun getNextTaskId(): Int {
        return try {
            val snapshot = firestore
                .collection(FirebaseManager.TASKS_COLLECTION)
                .get()
                .await()
            
            if (snapshot.documents.isEmpty()) {
                // Firebase vazio, começa do 1
                1
            } else {
                // Busca o maior ID numérico existente e incrementa
                val maxId = snapshot.documents
                    .mapNotNull { doc -> doc.id.toIntOrNull() }
                    .maxOrNull() ?: 0
                maxId + 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
            1 // Em caso de erro, começa do 1
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
