package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.Activity
import com.example.dataagrin.app.domain.repository.ActivityRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetActivitiesUseCaseTest {

    private val activityRepository: ActivityRepository = mockk()
    private val getActivitiesUseCase = GetActivitiesUseCase(activityRepository)

    @Test
    fun `invoke should return activities from repository`() = runBlocking {
        val fakeActivities = listOf(Activity(1, "Planting", "Area 51", "08:00", "10:00", "Notes"))
        coEvery { activityRepository.getAllActivities() } returns flowOf(fakeActivities)

        val result = getActivitiesUseCase.invoke().first()

        assertEquals(fakeActivities, result)
        coVerify(exactly = 1) { activityRepository.getAllActivities() }
    }
}
