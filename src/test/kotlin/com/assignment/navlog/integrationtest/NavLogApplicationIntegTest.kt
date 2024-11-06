package com.assignment.navlog.integrationtest

import com.assignment.navlog.model.NavigationLog
import com.assignment.navlog.repository.RouteRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.org.bouncycastle.util.Strings
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class NavLogApplicationIntegTest {
    @Autowired
    private val mockMvc: MockMvc? = null

    @Autowired
    private val routeRepository: RouteRepository? = null

    @Autowired
    private val objectMapper: ObjectMapper? = null

    @Test
    @Order(value = 1)
    fun testConnectionToDatabase() {
        Assertions.assertNotNull(routeRepository)
        Assertions.assertNotNull(objectMapper)
    }

    @Test
    @Order(value = 2)
    @Throws(
        Exception::class
    )
    fun testGetAllNavigationDataFailed() {
        val result = mockMvc!!.perform(MockMvcRequestBuilders.get("/navigation/navlog"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val expected: List<NavigationLog> =
            objectMapper!!.readerForListOf(NavigationLog::class.java).readValue(result.response.contentAsString)
        Assertions.assertNotNull(expected)
        Assertions.assertTrue(expected.isEmpty())
        Assertions.assertEquals(expected.size, routeRepository?.findAll()!!.count())
    }

    @Test
    @Order(value = 3)
    @Throws(
        Exception::class
    )
    fun testUploadNavigationData() {
        val data = Strings.fromByteArray(Files.readAllBytes(Paths.get("$PATH_TEMPLATE/request/upload_data.txt")))
        mockMvc!!.perform(
            MockMvcRequestBuilders.post("/navigation/upload/ABCD/PQRS")
                .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                .content(data)
        ).andExpect(MockMvcResultMatchers.status().isCreated)

        val expected = routeRepository?.findAll()
        Assertions.assertNotNull(expected)
        Assertions.assertEquals(1, expected!!.count())
        Assertions.assertNotNull(expected.first().waypoints)
        Assertions.assertEquals(2, expected.first().waypoints.size)
    }

    @Test
    @Order(value = 4)
    @Throws(
        Exception::class
    )
    fun testGetAllNavigationData() {
        mockMvc!!.perform(MockMvcRequestBuilders.get("/navigation/navlog"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        val expected = routeRepository?.findAll()
        val str = objectMapper!!.writerWithDefaultPrettyPrinter().writeValueAsString(expected)
        Assertions.assertNotNull(expected)
        Assertions.assertEquals(1, expected!!.count())
        Assertions.assertNotNull(expected.first().waypoints)
        Assertions.assertEquals(2, expected.first().waypoints.size)
    }

    @Test
    @Order(value = 5)
    @Throws(
        Exception::class
    )
    fun testGetNavigationDataById() {
        val expected = objectMapper!!.readValue(File("$PATH_TEMPLATE/response/navlog_2wp.json"), NavigationLog::class.java)
        mockMvc!!.perform(MockMvcRequestBuilders.get("/navigation/navlog/1"))
            .andExpect(MockMvcResultMatchers.status().isOk)
        val dbValue = routeRepository!!.findById(1)
        Assertions.assertTrue(dbValue.isPresent)
        val route = dbValue.get()
        Assertions.assertEquals(expected.departurePort, route.departurePort)
        Assertions.assertEquals(expected.arrivalPort, route.arrivalPort)
        Assertions.assertEquals(2, route.waypoints.size)
        Assertions.assertEquals(expected.waypoints.last().waypointName, route.arrivalPort)
    }

    @Test
    @Order(value = 6)
    @Throws(
        Exception::class
    )
    fun testGetNavigationDataByIdFailed() {
        mockMvc!!.perform(MockMvcRequestBuilders.get("/navigation/navlog/2"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    @Order(value = 7)
    @Throws(
        Exception::class
    )
    fun testUploadNavigationData_withInstruction() {
        val data = Strings.fromByteArray(Files.readAllBytes(Paths.get("$PATH_TEMPLATE/request/upload_data_with_instructions.txt")))
        mockMvc!!.perform(
            MockMvcRequestBuilders.post("/navigation/upload/EFGH/PQRS")
                .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                .content(data)
        ).andExpect(MockMvcResultMatchers.status().isCreated)

        val expected = routeRepository?.findAll()
        Assertions.assertNotNull(expected)
        Assertions.assertEquals(2, expected!!.count())
        Assertions.assertNotNull(expected.last().waypoints)
        Assertions.assertEquals(3, expected.last().waypoints.size)
        Assertions.assertTrue(expected.last().waypoints[1].instruction!!.isNotBlank())
    }

    @Test
    @Order(value = 8)
    @Throws(
        Exception::class
    )
    fun testDeleteNavigationDataById() {
        mockMvc!!.perform(MockMvcRequestBuilders.delete("/navigation/navlog/1"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    companion object {
        private const val PATH_TEMPLATE = "src/test/resources/"

        @Container
        private val postgreSQLContainer = PostgreSQLContainer("postgres:16")
            .withDatabaseName("integration-tests-db").withUsername("username").withPassword("password")

        init {
            postgreSQLContainer.setWaitStrategy(
                LogMessageWaitStrategy()
                    .withRegEx(".*database system is ready to accept connections.*\\s")
                    .withTimes(1)
                    .withStartupTimeout(Duration.ofMinutes(1))
            )
            postgreSQLContainer.start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
            registry.add("spring.liquibase.contexts") { "!prod" }
        }
    }
}