package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.Activity
import com.example.dataagrin.app.domain.repository.ActivityRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class InsertActivityUseCaseTest {

    private val activityRepository: ActivityRepository = mockk()
    private val insertActivityUseCase = InsertActivityUseCase(activityRepository)

    @Test
    fun `invoke should call insertActivity on repository`() = runBlocking {
        val fakeActivity = Activity(1, "Planting", "Area 51", "08:00", "10:00", "Notes")
        coEvery { activityRepository.insertActivity(fakeActivity) } returns Unit

        insertActivityUseCase.invoke(fakeActivity)

        coVerify(exactly = 1) { activityRepository.insertActivity(fakeActivity) }
    }
}
