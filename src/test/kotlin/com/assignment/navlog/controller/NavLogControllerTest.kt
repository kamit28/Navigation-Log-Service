package com.assignment.navlog.controller

import com.assignment.navlog.model.NavigationLog
import com.assignment.navlog.service.NavLogService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit4.SpringRunner
import java.io.File
import kotlin.test.assertEquals

@RunWith(SpringRunner::class)
internal class NavLogControllerTest {
    private lateinit var service: NavLogService
    private lateinit var controller: NavLogController

    private lateinit var objectMapper: ObjectMapper

    companion object {
        private const val PATH_TEMPLATE = "src/test/resources/response/"
    }

    @BeforeEach
    fun setUp() {
        objectMapper = jacksonObjectMapper()
        service = mockk<NavLogService>(relaxed = true)
        controller = NavLogController(service)
    }

    @Test
    fun `should return navlog`() {
        val navlog = objectMapper.readValue(File(PATH_TEMPLATE + "navlog.json"), NavigationLog::class.java)
        val expected = ResponseEntity.ok(navlog)

        every { service.getNavLogFor(1L) } returns navlog

        val testResponse = controller.getNavData(1)

        assertEquals(expected.statusCode, testResponse.statusCode)
        assertEquals(objectMapper.writeValueAsString(expected.body), objectMapper.writeValueAsString(testResponse.body))
    }

    @Test
    fun `should return all navlogs`() {
        val navlogs: List<NavigationLog> =
            objectMapper.readerForListOf(NavigationLog::class.java).readValue(File(PATH_TEMPLATE + "navlog_all.json"))
        val expected = ResponseEntity.ok(navlogs)

        every { service.getNavLogs() } returns navlogs

        val testResponse = controller.getAllNavData()

        assertEquals(expected.statusCode, testResponse.statusCode)
        assertEquals(objectMapper.writeValueAsString(expected.body), objectMapper.writeValueAsString(testResponse.body))
    }

    @Test
    fun `should delete navlog`() {
        val expected = ResponseEntity.ok().build<Unit>()

        every { service.deleteNavLogFor(1L) } returns Unit

        val testResponse = controller.deleteNavData(1L)

        assertEquals(expected.statusCode, testResponse.statusCode)
    }

    @Test
    fun `should create navlog`() {
        val data = """WPT     S/H  DST FL  ETI   G/S WIND  MACH\n\n
                SYDNY   08.2 047 231 00.12 413 12039 .676\n\n
                THROW   07.6 022 309 00.03 455 14028 .776"""

        val expected = ResponseEntity.status(HttpStatus.CREATED).build<Unit>()

        every { service.processNavData("ABCD", "PQRS", data) } returns 1L

        val testResponse = controller.uploadNavData("ABCD", "PQRS", data)

        assertEquals(expected.statusCode, testResponse.statusCode)
    }
}