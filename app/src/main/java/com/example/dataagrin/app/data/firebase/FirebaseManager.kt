package com.example.dataagrin.app.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

object FirebaseManager {
    val firestore: FirebaseFirestore = Firebase.firestore

    fun initialize() {
        // Habilitar offline persistence
        try {
            firestore.firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        } catch (_: Exception) {
            // Offline persistence já está habilitado
        }
    }

    // Collections
    const val TASKS_COLLECTION = "tasks"
    const val TASK_REGISTRY_COLLECTION = "task_registries"
}
