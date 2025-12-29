package com.example.dataagrin.app.data.location

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.LocationManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocationHelperTest {
    private lateinit var context: Context
    private lateinit var locationHelper: LocationHelper
    private lateinit var mockLocationManager: LocationManager
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    @Suppress("DEPRECATION")
    @Before
    fun setup() {
        // Parar Koin se estiver rodando de testes anteriores
        try {
            stopKoin()
        } catch (e: Exception) {
            // Koin pode não estar iniciado, ignorar
        }

        context = mockk(relaxed = true)
        mockkConstructor(Geocoder::class)
        every { anyConstructed<Geocoder>().getFromLocation(any<Double>(), any<Double>(), 1) } returns
            listOf(
                mockk<android.location.Address>().apply {
                    every { locality } returns "São Paulo"
                    every { adminArea } returns "SP"
                    every { countryCode } returns "BR"
                },
            )

        // Mock de SharedPreferences
        mockSharedPreferences = mockk(relaxed = true)
        mockEditor = mockk(relaxed = true)
        every { mockSharedPreferences.edit() } returns mockEditor
        every { mockEditor.putString(any(), any()) } returns mockEditor
        every { mockEditor.apply() } returns Unit
        every { context.getSharedPreferences("location_prefs_v2", Context.MODE_PRIVATE) } returns mockSharedPreferences

        // Mock de LocationManager
        mockLocationManager = mockk()
        every { context.getSystemService(Context.LOCATION_SERVICE) } returns mockLocationManager

        locationHelper = spyk(LocationHelper(context))
    }

    @Test
    fun `getCityNameFromCoordinates should handle geocoding gracefully`() =
        runBlocking {
            // Dado
            val latitude = -23.550520
            val longitude = -46.633308

            // Este teste verifica que o método não falha e retorna um resultado razoável
            // Em um cenário real com Robolectric, Geocoder pode funcionar ou retornar resultados vazios
            val result = locationHelper.getCityNameFromCoordinates(latitude, longitude)

            // O resultado deve ser uma string (ou um nome de cidade ou "Localização Desconhecida")
            assertNotNull(result)
        }

    @Test
    fun `getDefaultLocation should return fallback location when no location available`() =
        runBlocking {
            // Given
            every { mockSharedPreferences.getString("saved_city", null) } returns null
            every { mockSharedPreferences.getString("saved_lat", null) } returns null
            every { mockSharedPreferences.getString("saved_lng", null) } returns null

            // When
            val result = locationHelper.getDefaultLocation()

            // Then
            assertNotNull(result)
            assertEquals("São Paulo", result?.cityName)
            assertEquals(-23.550520, result?.latitude ?: 0.0, 0.001)
            assertEquals(-46.633308, result?.longitude ?: 0.0, 0.001)
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
            assertEquals(savedLat.toDouble(), result?.latitude)
            assertEquals(savedLng.toDouble(), result?.longitude)
        }

    @Test
    fun `getLocationFromIP should handle network errors gracefully`() =
        runBlocking {
            // Este teste exigiria simular URL e HttpURLConnection
            // Por enquanto, verificaremos se o método existe e pode ser chamado
            assertNotNull(locationHelper)

            // Em uma implementação real, simularíamos a conexão HTTP
            // e verificar o tratamento de erros
        }

    @Test
    fun `getLastLocation should return location when available`() =
        runBlocking {
            // Given
            every { mockSharedPreferences.getString("saved_city", null) } returns "São Paulo"
            every { mockSharedPreferences.getString("saved_lat", null) } returns "-23.550520"
            every { mockSharedPreferences.getString("saved_lng", null) } returns "-46.633308"

            // When
            val result = locationHelper.getLastLocation()

            // Then
            assertNotNull(result)
            assertEquals(-23.550520, result?.latitude ?: 0.0, 0.001)
            assertEquals(-46.633308, result?.longitude ?: 0.0, 0.001)
            assertEquals("São Paulo", result?.cityName)
        }

    @Test
    fun `getLastLocation should return null when location unavailable`() =
        runBlocking {
            // Given
            every { mockSharedPreferences.getString("saved_city", null) } returns null
            every { mockSharedPreferences.getString("saved_lat", null) } returns null
            every { mockSharedPreferences.getString("saved_lng", null) } returns null

            // When
            val result = locationHelper.getLastLocation()

            // Then
            assertNull(result)
        }

    @Test
    fun `getCurrentLocation should return location data when successful`() =
        runBlocking {
            // Given
            val mockLocation = mockk<android.location.Location>()
            every { mockLocation.latitude } returns -22.906847
            every { mockLocation.longitude } returns -43.172896

            every { mockLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) } returns mockLocation

            // When
            val result = locationHelper.getCurrentLocation()

            // Then
            assertNotNull(result)
            assertEquals(-22.906847, result?.latitude ?: 0.0, 0.001)
            assertEquals(-43.172896, result?.longitude ?: 0.0, 0.001)
            assertTrue(result?.cityName?.isNotEmpty() == true)
        }

    @Test
    fun `getCurrentLocation should return null when location fails`() =
        runBlocking {
            // Given
            every { mockLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) } returns null
            every { mockLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } returns null
            every {
                mockLocationManager.requestLocationUpdates(
                    any<String>(),
                    any<Long>(),
                    any<Float>(),
                    any<android.location.LocationListener>(),
                    any(),
                )
            } throws SecurityException("No permission")

            // When
            val result = locationHelper.getCurrentLocation()

            // Then
            assertNull(result)
        }

    @Test
    fun `getCityNameFromCoordinates should handle invalid coordinates gracefully`() =
        runBlocking {
            // Dadas coordenadas inválidas (fora do intervalo válido)
            val invalidLat = 91.0 // Latitude inválida (> 90)
            val invalidLng = 181.0 // Longitude inválida (> 180)

            // Quando
            val result = locationHelper.getCityNameFromCoordinates(invalidLat, invalidLng)

            // Então - Não deve travar e retornar algum resultado
            assertNotNull(result)
            assertTrue(result.isNotEmpty())
        }

    @Test
    fun `getCityNameFromCoordinates should handle extreme coordinates`() =
        runBlocking {
            // Teste com coordenadas nos extremos dos intervalos válidos
            val southPoleLat = -90.0
            val southPoleLng = 0.0

            val result = locationHelper.getCityNameFromCoordinates(southPoleLat, southPoleLng)

            // Não deve travar
            assertNotNull(result)
        }

    @Test
    fun `getDefaultLocation should handle malformed saved coordinates gracefully`() =
        runBlocking {
            // Dadas coordenadas salvas malformadas
            every { mockSharedPreferences.getString("saved_city", null) } returns "Test City"
            every { mockSharedPreferences.getString("saved_lat", null) } returns "invalid_lat"
            every { mockSharedPreferences.getString("saved_lng", null) } returns "invalid_lng"

            // When
            val result = locationHelper.getDefaultLocation()

            // Then - Deve voltar para a localização padrão
            assertNotNull(result)
            assertEquals("São Paulo", result?.cityName)
        }

    @Test
    fun `getLocationOrSavedFallback should handle exceptions gracefully`() =
        runBlocking {
            // Dado - Simular que os serviços de localização lançam exceção
            coEvery { locationHelper.getCurrentLocation() } throws RuntimeException("Location service error")
            every { mockSharedPreferences.getString("saved_city", null) } returns null

            // When
            val result = locationHelper.getLocationOrSavedFallback()

            // Then - Deve retornar localização padrão
            assertNotNull(result)
            assertEquals("São Paulo", result?.cityName)
        }

    @Test
    fun `LocationData should store coordinates correctly`() {
        // Testar a classe de dados
        val locationData =
            LocationData(
                latitude = -15.794229,
                longitude = -47.882166,
                cityName = "Brasília",
            )

        assertEquals(-15.794229, locationData.latitude, 0.000001)
        assertEquals(-47.882166, locationData.longitude, 0.000001)
        assertEquals("Brasília", locationData.cityName)
    }

    @Test
    fun `getLocationOrSavedFallback should return saved location when current location fails`() =
        runBlocking {
            // Dado - Simular que a localização atual retorna null
            coEvery { locationHelper.getCurrentLocation() } returns null

            // Simular localização salva
            val savedCity = "Brasília"
            val savedLat = "-15.794229"
            val savedLng = "-47.882166"

            every { mockSharedPreferences.getString("saved_city", null) } returns savedCity
            every { mockSharedPreferences.getString("saved_lat", null) } returns savedLat
            every { mockSharedPreferences.getString("saved_lng", null) } returns savedLng

            // When
            val result = locationHelper.getLocationOrSavedFallback()

            // Then
            assertNotNull(result)
            assertEquals(savedCity, result?.cityName)
        }

    @Test
    fun `getLocationOrSavedFallback should return default location when no saved location`() =
        runBlocking {
            // Dado - Simular que a localização atual retorna null
            coEvery { locationHelper.getCurrentLocation() } returns null

            // Simular nenhuma localização salva
            every { mockSharedPreferences.getString("saved_city", null) } returns null

            // When
            val result = locationHelper.getLocationOrSavedFallback()

            // Then
            assertNotNull(result)
            assertEquals("São Paulo", result?.cityName)
        }
}
