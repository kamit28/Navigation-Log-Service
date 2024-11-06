package com.assignment.navlog.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "waypoints")
data class Waypoint(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "name")
    var name: String,

    @Column(name = "safety_height")
    var safetyHeight: Int,

    @Column(name = "distance")
    var distance: Int,

    @Column(name = "flight_level")
    var flightLevel: Int,

    @Column(name = "eti")
    var estimatedTimeInterval: String,

    @Column(name = "ground_speed")
    var groundSpeed: Int,

    @Column(name = "mach")
    var mach: Double,

    @Column(name = "wind_speed")
    var windSpeed: Int,

    @Column(name = "wind_direction")
    var windDirection: Int,

    @Column(name = "instruction", nullable = true)
    var instruction: String? = null,
)