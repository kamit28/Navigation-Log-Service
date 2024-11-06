package com.assignment.navlog.model

data class WaypointDTO(
    val waypointName: String,
    val safetyHeight: Int,
    val distance: Int,
    val flightLevel: Int,
    val estimatedTimeInterval: String,
    val groundSpeed: Int,
    val mach: Double,
    val windSpeed: Int,
    val windDirection: Int,
    val instruction: String? = null,
)
