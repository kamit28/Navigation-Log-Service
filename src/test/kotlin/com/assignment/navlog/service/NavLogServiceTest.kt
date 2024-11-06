package com.assignment.navlog.service

import com.assignment.navlog.entity.Route
import com.assignment.navlog.repository.RouteRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.shaded.org.bouncycastle.util.Strings
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@ExtendWith(SpringExtension::class)
internal class NavLogServiceTest {
    private val routeRepository: RouteRepository = mockk()

    private val navLogService: NavLogService = NavLogService(routeRepository)

    private val objectMapper = ObjectMapper()

    private lateinit var dbResult: List<Route>

    companion object {
        private const val PATH_TEMPLATE = "src/test/resources/"
    }

    @BeforeEach
    fun setUp() {
        dbResult =
            objectMapper.readerForListOf(Route::class.java).readValue(File("$PATH_TEMPLATE/response/all_routes.json"))
    }

    @Test
    fun `should get all routes`() {
        every { routeRepository.findAll() } returns dbResult

        val result = navLogService.getNavLogs()

        Assertions.assertTrue(result.isNotEmpty())
        Assertions.assertEquals(dbResult.size, result.size)
        Assertions.assertEquals(dbResult[0].waypoints.size, result[0].waypoints.size)

        val wpAsStr = objectMapper.writeValueAsString(dbResult[0].waypoints.toWaypointDTOs())
        Assertions.assertEquals(wpAsStr, objectMapper.writeValueAsString(result[0].waypoints))
    }

    @Test
    fun `should get route for id`() {
        val route = dbResult[0]
        every { routeRepository.findById(any()) } returns Optional.of(route)

        val result = navLogService.getNavLogFor(1)

        Assertions.assertNotNull(result)
        Assertions.assertEquals(route.departurePort, result.departurePort)
        Assertions.assertEquals(route.arrivalPort, result.arrivalPort)
        Assertions.assertEquals(route.waypoints.size, result.waypoints.size)

        val wpAsStr = objectMapper.writeValueAsString(route.waypoints.toWaypointDTOs())
        Assertions.assertEquals(wpAsStr, objectMapper.writeValueAsString(result.waypoints))
    }

    @Test
    fun `should create a route`() {
        val route = dbResult[0]

        val data = Strings.fromByteArray(Files.readAllBytes(Paths.get("${PATH_TEMPLATE}/request/upload_data.txt")))

        every { routeRepository.save(any()) } returns route

        val result = navLogService.processNavData("ABCD", "PQRS", data)

        Assertions.assertNotNull(result)
        Assertions.assertEquals(route.id, result)
    }
}