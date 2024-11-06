package com.assignment.navlog.model

data class NavigationLog(
    val id : Long,
    val departurePort: String,
    val arrivalPort: String,
    val totalFlightTime: String,
    val totalDistance: Int,
    val averageGroundSpeed: Double,
    val averageMach: Double,
    val waypoints: List<WaypointDTO>

)