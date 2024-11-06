package com.assignment.navlog.service

import com.assignment.navlog.entity.Route
import com.assignment.navlog.entity.Waypoint
import com.assignment.navlog.exception.ResourceNotFoundException
import com.assignment.navlog.model.NavigationLog
import com.assignment.navlog.model.WaypointDTO
import com.assignment.navlog.repository.RouteRepository
import com.assignment.navlog.utils.toDouble
import com.assignment.navlog.utils.toISO8601Duration
import com.assignment.navlog.utils.toInt
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.time.Duration

@Service
class NavLogService(
    private val routeRepository: RouteRepository,
) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(NavLogService::class.java)
    }

    fun getNavLogs(): List<NavigationLog> {
        return routeRepository.findAll().map { route ->
            NavigationLog(
                id = route.id!!,
                departurePort = route.departurePort,
                arrivalPort = route.arrivalPort,
                totalFlightTime = route.waypoints.totalFlightTime(),
                totalDistance = route.waypoints.totalDistance(),
                averageGroundSpeed = route.waypoints.averageGroundSpeed(),
                averageMach = route.waypoints.averageMach(),
                waypoints = route.waypoints.toWaypointDTOs(),
            )
        }
    }

    fun getNavLogFor(id: Long): NavigationLog {
        return routeRepository.findRouteById(id)?.let { route ->
            NavigationLog(
                id = id,
                departurePort = route.departurePort,
                arrivalPort = route.arrivalPort,
                totalFlightTime = route.waypoints.totalFlightTime(),
                totalDistance = route.waypoints.totalDistance(),
                averageGroundSpeed = route.waypoints.averageGroundSpeed(),
                averageMach = route.waypoints.averageMach(),
                waypoints = route.waypoints.toWaypointDTOs(),
            )
        } ?: throw ResourceNotFoundException("Navigation log not found for $id")
    }

    fun processNavData(departurePort: String, arrivalPort: String, data: String): Long? {
        val waypoints = processWayPoints(data)
        if (waypoints.last().name.equals(arrivalPort, true)) {
            val newRoute = Route(
                departurePort = departurePort,
                arrivalPort = arrivalPort,
                waypoints = waypoints
            )

            val savedEntity = routeRepository.save(newRoute)
            return savedEntity.id
        }
        LOG.error("arrivalPort is not same as last waypoint name")
        throw IllegalArgumentException("arrivalPort is not same as last waypoint name")
    }

    fun deleteNavLogFor(id: Long) = routeRepository.deleteById(id)

    private fun processWayPoints(data: String): List<Waypoint> {
        val lines = data.split("\n\n")
        return lines.drop(1).map {
            processLine(it)
        }
    }

    private fun processLine(line: String): Waypoint {
        val tokens = line.split("\n")
        println(tokens)
        val navData = tokens[0].replace("\\s+".toRegex(), " ").split(" ")
        if (navData.size != 8) {
            LOG.error("Input data format exception")
            throw IllegalArgumentException("Input data format exception")
        }

        val instruction = if (tokens.size == 2) tokens[1] else ""

        return Waypoint(
            name = navData[0],
            safetyHeight = (toDouble(navData[1]) * 1000).toInt(),
            distance = toInt(navData[2]),
            flightLevel = toInt(navData[3]),
            estimatedTimeInterval = toISO8601Duration(navData[4]),
            groundSpeed = toInt(navData[5]),
            windSpeed = toInt(navData[6].takeLast(2)),
            windDirection = toInt(navData[6].take(3)),
            mach = toDouble(navData[7]),
            instruction = instruction.trim()
        )
    }
}

fun List<Waypoint>.toWaypointDTOs() =
    this.map { wp ->
        WaypointDTO(
            waypointName = wp.name,
            safetyHeight = wp.safetyHeight,
            distance = wp.distance,
            flightLevel = wp.flightLevel,
            estimatedTimeInterval = wp.estimatedTimeInterval,
            groundSpeed = wp.groundSpeed,
            windSpeed = wp.windSpeed,
            windDirection = wp.windDirection,
            mach = wp.mach,
            instruction = wp.instruction
        )
    }

fun List<Waypoint>.totalDistance(): Int =
    this.sumOf { it.distance }

fun List<Waypoint>.totalFlightTime(): String =
    this.map { wp ->
        Duration.parseIsoString(wp.estimatedTimeInterval)
    }.fold(Duration.ZERO) { sum, d -> sum.plus(d) }.toIsoString()

fun List<Waypoint>.averageGroundSpeed(): Double =
    this.map { it.groundSpeed }.average()

fun List<Waypoint>.averageMach(): Double =
    this.map { it.mach }.average()
