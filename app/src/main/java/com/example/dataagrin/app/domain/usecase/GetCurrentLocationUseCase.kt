package com.example.dataagrin.app.domain.usecase

import android.location.Location
import com.example.dataagrin.app.presentation.location.LocationService

class GetCurrentLocationUseCase(private val locationService: LocationService) {
    suspend operator fun invoke(): Location? {
        return locationService.getCurrentLocation()
    }
}
