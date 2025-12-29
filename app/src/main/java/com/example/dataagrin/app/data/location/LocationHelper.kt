package com.example.dataagrin.app.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
)

class LocationHelper(
    private val context: Context,
) {
    companion object {
        private const val TAG = "LocationHelper"
    }

    private val geocoder = Geocoder(context, Locale.forLanguageTag("pt-BR"))
    private val prefs = context.getSharedPreferences("location_prefs_v2", Context.MODE_PRIVATE)

    // Mapa de estados brasileiros para abreviações
    private val brazilianStates =
        mapOf(
            "Acre" to "AC",
            "Alagoas" to "AL",
            "Amapá" to "AP",
            "Amazonas" to "AM",
            "Bahia" to "BA",
            "Ceará" to "CE",
            "Distrito Federal" to "DF",
            "Espírito Santo" to "ES",
            "Goiás" to "GO",
            "Maranhão" to "MA",
            "Mato Grosso" to "MT",
            "Mato Grosso do Sul" to "MS",
            "Minas Gerais" to "MG",
            "Pará" to "PA",
            "Paraíba" to "PB",
            "Paraná" to "PR",
            "Pernambuco" to "PE",
            "Piauí" to "PI",
            "Rio de Janeiro" to "RJ",
            "Rio Grande do Norte" to "RN",
            "Rio Grande do Sul" to "RS",
            "Rondônia" to "RO",
            "Roraima" to "RR",
            "Santa Catarina" to "SC",
            "São Paulo" to "SP",
            "Sergipe" to "SE",
            "Tocantins" to "TO",
        )

    /**
     * Salva a última localização conhecida
     */
    fun saveLastLocation(location: LocationData) {
        prefs.edit {
            putString("saved_city", location.cityName)
            putString("saved_lat", location.latitude.toString())
            putString("saved_lng", location.longitude.toString())
        }
    }

    /**
     * Obtém a última localização conhecida
     */
    fun getLastLocation(): LocationData? {
        val city = prefs.getString("saved_city", null)
        val latStr = prefs.getString("saved_lat", null)
        val lngStr = prefs.getString("saved_lng", null)
        if (city != null && latStr != null && lngStr != null) {
            try {
                val lat = latStr.toDouble()
                val lng = lngStr.toDouble()
                return LocationData(lat, lng, city)
            } catch (_: Exception) {
                // Dados salvos inválidos, ignorar
            }
        }
        return null
    }

    /**
     * Obtém o nome da cidade a partir de coordenadas usando Geocoder
     */
    @Suppress("DEPRECATION")
    suspend fun getCityNameFromCoordinates(
        latitude: Double,
        longitude: Double,
    ): String =
        withContext(Dispatchers.IO) {
            try {
                // Timeout de 5 segundos para geocodificação
                val addresses =
                    withTimeoutOrNull(5000L) {
                        geocoder.getFromLocation(latitude, longitude, 1)
                    }
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: "Localização Desconhecida"

                    // Para cidades brasileiras, adiciona a sigla do estado
                    if (address.countryCode == "BR" && address.adminArea != null) {
                        val stateAbbrev = brazilianStates[address.adminArea] ?: address.adminArea
                        "$city - $stateAbbrev"
                    } else if (address.countryName != null) {
                        "$city - ${address.countryName}"
                    } else {
                        city
                    }
                } else {
                    "Localização Desconhecida"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao geocodificar coordenadas", e)
                "Localização Desconhecida"
            }
        }

    /**
     * Verifica se as permissões de localização foram concedidas
     */
    fun hasLocationPermission(): Boolean {
        val fineLocation =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    /**
     * Retorna localização padrão (última conhecida ou São Paulo como fallback)
     */
    fun getDefaultLocation(): LocationData = getLastLocation() ?: LocationData(-23.550520, -46.633308, "São Paulo")

    /**
     * Tenta obter localização atual usando GPS, com fallback
     */
    suspend fun getLocationOrSavedFallback(): LocationData? {
        Log.d(TAG, "getLocationOrSavedFallback: Iniciando")
        return try {
            getCurrentLocation()
        } catch (e: Exception) {
            Log.e(TAG, "getLocationOrSavedFallback: Erro em getCurrentLocation", e)
            null
        } ?: getLastLocation() ?: LocationData(-23.550520, -46.633308, "São Paulo").also {
            Log.d(TAG, "getLocationOrSavedFallback: Usando fallback São Paulo")
        }
    }

    /**
     * Obtém localização atual usando LocationManager
     */
    @Suppress("MissingPermission")
    suspend fun getCurrentLocation(): LocationData? {
        Log.d(TAG, "getCurrentLocation: Iniciando obtenção de localização")
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "getCurrentLocation: Verificando permissões")
                if (!hasLocationPermission()) {
                    Log.d(TAG, "getCurrentLocation: Sem permissões")
                    return@withContext null
                }

                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                Log.d(TAG, "getCurrentLocation: LocationManager obtido")

                // Tenta last known primeiro
                Log.d(TAG, "getCurrentLocation: Tentando last known location")
                val lastLocation =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                if (lastLocation != null) {
                    Log.d(TAG, "getCurrentLocation: Last location encontrada: ${lastLocation.latitude}, ${lastLocation.longitude}")
                    val cityName = getCityNameFromCoordinates(lastLocation.latitude, lastLocation.longitude)
                    Log.d(TAG, "getCurrentLocation: Cidade obtida: $cityName")
                    return@withContext LocationData(lastLocation.latitude, lastLocation.longitude, cityName)
                }

                Log.d(TAG, "getCurrentLocation: Nenhuma last location, solicitando updates")
                // Se não há last known, solicita atualização
                val location =
                    withTimeoutOrNull(10000L) {
                        // 10 segundos timeout
                        suspendCancellableCoroutine { continuation: CancellableContinuation<Location?> ->
                            Log.d(TAG, "getCurrentLocation: Criando LocationListener")
                            var isResumed = false
                            val locationListener =
                                object : LocationListener {
                                    override fun onLocationChanged(location: Location) {
                                        Log.d(TAG, "getCurrentLocation: onLocationChanged: ${location.latitude}, ${location.longitude}")
                                        if (!isResumed) {
                                            isResumed = true
                                            locationManager.removeUpdates(this)
                                            continuation.resume(location)
                                        }
                                    }

                                    override fun onProviderEnabled(provider: String) {}

                                    override fun onProviderDisabled(provider: String) {
                                        if (!isResumed) {
                                            isResumed = true
                                            continuation.resume(null)
                                        }
                                    }

                                    @Suppress("OVERRIDE_DEPRECATION")
                                    override fun onStatusChanged(
                                        provider: String?,
                                        status: Int,
                                        extras: Bundle?,
                                    ) {}
                                }

                            try {
                                Log.d(TAG, "getCurrentLocation: Solicitando location updates")
                                locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    0L,
                                    0f,
                                    locationListener,
                                    Looper.getMainLooper(),
                                )
                                // Timeout de 10 segundos
                                continuation.invokeOnCancellation {
                                    Log.d(TAG, "getCurrentLocation: Coroutine cancelada, removendo updates")
                                    locationManager.removeUpdates(locationListener)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "getCurrentLocation: Erro ao solicitar updates", e)
                                if (!isResumed) {
                                    isResumed = true
                                    continuation.resume(null)
                                }
                            }
                        }
                    }

                if (location != null) {
                    Log.d(TAG, "getCurrentLocation: Location obtida: ${location.latitude}, ${location.longitude}")
                    val cityName = getCityNameFromCoordinates(location.latitude, location.longitude)
                    Log.d(TAG, "getCurrentLocation: Cidade obtida: $cityName")
                    LocationData(location.latitude, location.longitude, cityName)
                } else {
                    Log.d(TAG, "getCurrentLocation: Nenhuma location obtida")
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "getCurrentLocation: Erro geral", e)
                null
            }
        }
    }
}
