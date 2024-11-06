package com.assignment.navlog.entity

import jakarta.persistence.*

@Entity
@Table(name = "routes")
data class Route (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var departurePort: String,

    @Column(nullable = false)
    var arrivalPort: String,

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "route_id", nullable = false)
    val waypoints: List<Waypoint> = mutableListOf()
)