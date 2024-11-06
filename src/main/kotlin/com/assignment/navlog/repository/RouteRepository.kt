package com.assignment.navlog.repository

import com.assignment.navlog.entity.Route
import org.springframework.data.repository.CrudRepository

interface RouteRepository: CrudRepository<Route, Long> {
    fun findRouteById(id: Long): Route?
}