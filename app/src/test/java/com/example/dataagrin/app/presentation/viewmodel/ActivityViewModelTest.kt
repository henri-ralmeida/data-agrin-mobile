package com.example.dataagrin.app.presentation.viewmodel

import com.example.dataagrin.app.MainCoroutineRule
import com.example.dataagrin.app.domain.model.Activity
import com.example.dataagrin.app.domain.usecase.GetActivitiesUseCase
import com.example.dataagrin.app.domain.usecase.InsertActivityUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ActivityViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val getActivitiesUseCase: GetActivitiesUseCase = mockk()
    private val insertActivityUseCase: InsertActivityUseCase = mockk(relaxed = true)
    private lateinit var viewModel: ActivityViewModel

    @Before
    fun setUp() {
        val fakeActivities = listOf(Activity(1, "Planting", "Area 51", "08:00", "10:00", "Notes"))
        coEvery { getActivitiesUseCase() } returns flowOf(fakeActivities)
        viewModel = ActivityViewModel(getActivitiesUseCase, insertActivityUseCase)
    }

    @Test
    fun `activities StateFlow should be updated on init`() = runTest {
        val activities = viewModel.activities.first()
        assertEquals(1, activities.size)
        assertEquals("Planting", activities[0].type)
    }

    @Test
    fun `insertActivity should call InsertActivityUseCase`() = runTest {
        val newActivity = Activity(2, "Harvesting", "Area 52", "14:00", "16:00", "More notes")
        val job = launch {
            viewModel.insertActivity(newActivity)
        }
        job.join()
        coVerify(exactly = 1) { insertActivityUseCase(newActivity) }
    }
}
