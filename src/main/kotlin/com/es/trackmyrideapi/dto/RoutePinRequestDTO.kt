package com.es.trackmyrideapi.dto

class RoutePinRequestDTO (
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val description: String? = null,
    val routeId: Long
)