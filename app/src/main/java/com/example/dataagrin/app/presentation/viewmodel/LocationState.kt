package com.example.dataagrin.app.presentation.viewmodel

import com.example.dataagrin.app.data.location.LocationData

sealed class LocationState {
    object Loading : LocationState()

    object NoPermission : LocationState()

    object Unavailable : LocationState()

    data class Available(
        val location: LocationData,
        val isFromCache: Boolean = false,
    ) : LocationState()
}
