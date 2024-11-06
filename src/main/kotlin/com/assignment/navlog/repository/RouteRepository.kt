package com.assignment.navlog.repository

import com.assignment.navlog.entity.Route
import org.springframework.data.jpa.repository.JpaRepository

interface RouteRepository: JpaRepository<Route, Long> {
}