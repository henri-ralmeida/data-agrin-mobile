package com.example.dataagrin.app.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Test

class TaskFirestoreRepositoryTest {
    @Test
    fun `repository should be instantiated correctly`() {
        // Given - create a mock firestore instance
        val testFirestore = mockk<FirebaseFirestore>(relaxed = true)

        // When
        val repository = TaskFirestoreRepository(testFirestore)

        // Then
        assertNotNull(repository)
    }
}
