package com.example.dataagrin.app.data.location

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class LocationHelperSimpleTest {
    private lateinit var context: Context
    private lateinit var locationHelper: LocationHelper
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setup() {
        // Parar Koin se estiver rodando de testes anteriores
        try {
            stopKoin()
        } catch (e: Exception) {
            // Koin pode não estar iniciado, ignorar
        }

        context = RuntimeEnvironment.getApplication()

        // Mock de SharedPreferences
        mockSharedPreferences = mockk()
        mockEditor = mockk()
        every { mockEditor.putString(any(), any()) } returns mockEditor
        every { mockEditor.apply() } returns Unit

        // Mock do contexto para retornar nosso mock SharedPreferences
        val mockContext = mockk<Context>(relaxed = true)
        every { mockContext.getSharedPreferences("location_prefs_v2", Context.MODE_PRIVATE) } returns mockSharedPreferences

        // Criar LocationHelper com contexto mockado
        locationHelper = LocationHelper(mockContext)
    }

    @After
    fun tearDown() {
        try {
            stopKoin()
        } catch (e: Exception) {
            // Ignorar se Koin não estiver iniciado
        }
    }

    @Test
    fun `getDefaultLocation should return saved location when available`() =
        runBlocking {
            // Given
            val savedCity = "Rio de Janeiro"
            val savedLat = "-22.906847"
            val savedLng = "-43.172896"

            every { mockSharedPreferences.getString("saved_city", null) } returns savedCity
            every { mockSharedPreferences.getString("saved_lat", null) } returns savedLat
            every { mockSharedPreferences.getString("saved_lng", null) } returns savedLng

            // When
            val result = locationHelper.getDefaultLocation()

            // Then
            assertNotNull(result)
            assertEquals(savedCity, result?.cityName)
            assertEquals(savedLat.toDouble(), result?.latitude ?: 0.0, 0.001)
            assertEquals(savedLng.toDouble(), result?.longitude ?: 0.0, 0.001)
        }

    @Test
    fun `getDefaultLocation should return fallback location when no saved location`() =
        runBlocking {
            // Given
            every { mockSharedPreferences.getString("saved_city", null) } returns null
            every { mockSharedPreferences.getString("saved_lat", null) } returns null
            every { mockSharedPreferences.getString("saved_lng", null) } returns null

            // When
            val result = locationHelper.getDefaultLocation()

            // Then
            assertNotNull(result)
            // Deve retornar localização padrão de fallback (São Paulo)
            assertEquals("São Paulo", result?.cityName)
            assertEquals(-23.550520, result?.latitude ?: 0.0, 0.001)
            assertEquals(-46.633308, result?.longitude ?: 0.0, 0.001)
        }

    @Test
    fun `getDefaultLocation should handle invalid saved coordinates gracefully`() =
        runBlocking {
            // Given
            val savedCity = "Invalid City"
            val invalidLat = "invalid"
            val invalidLng = "also_invalid"

            every { mockSharedPreferences.getString("saved_city", null) } returns savedCity
            every { mockSharedPreferences.getString("saved_lat", null) } returns invalidLat
            every { mockSharedPreferences.getString("saved_lng", null) } returns invalidLng

            // When
            val result = locationHelper.getDefaultLocation()

            // Then
            assertNotNull(result)
            // Deve voltar para localização padrão quando o parsing falhar
            assertEquals("São Paulo", result?.cityName)
            assertEquals(-23.550520, result?.latitude ?: 0.0, 0.001)
            assertEquals(-46.633308, result?.longitude ?: 0.0, 0.001)
        }
}
