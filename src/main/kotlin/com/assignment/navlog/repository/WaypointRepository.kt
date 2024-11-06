package com.assignment.navlog.repository

import com.assignment.navlog.entity.Waypoint
import org.springframework.data.jpa.repository.JpaRepository

interface WaypointRepository: JpaRepository<Waypoint, Long> {
}