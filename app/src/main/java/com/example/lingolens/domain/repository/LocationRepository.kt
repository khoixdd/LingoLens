package com.example.lingolens.domain.repository

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
)

interface LocationRepository {
    suspend fun getCurrentLocation(): UserLocation?
    fun hasLocationPermission(): Boolean
}
