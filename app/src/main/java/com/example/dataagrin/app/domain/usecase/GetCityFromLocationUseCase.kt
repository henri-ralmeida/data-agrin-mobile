package com.example.dataagrin.app.domain.usecase

import android.content.Context
import android.location.Geocoder
import android.location.Location
import java.util.Locale

class GetCityFromLocationUseCase(private val context: Context) {
    fun invoke(location: Location): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        return addresses?.firstOrNull()?.locality ?: "Localização desconhecida"
    }
}
