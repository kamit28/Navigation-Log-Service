package com.assignment.navlog.controller

import com.assignment.navlog.model.NavigationLog
import com.assignment.navlog.service.NavLogService
import jakarta.validation.constraints.Size
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/navigation")
class NavLogController(
    private val navLogService: NavLogService,
) {
    companion object {
        val LOG: Logger = LoggerFactory.getLogger(NavLogController::class.java)
    }

    @PostMapping(
        value = ["/upload/{departurePort}/{arrivalPort}"],
        consumes = ["text/plain;charset=UTF-8"]
    )
    fun uploadNavData(
        @Size(min = 4, max = 4, message = "should be 4 characters long")
        @PathVariable("departurePort") departurePort: String,
        @Size(min = 4, max = 4, message = "should be 4 characters long")
        @PathVariable("arrivalPort") arrivalPort: String,
        @RequestBody data: String
    ): ResponseEntity<Unit> {
        LOG.info("Message request body = {}", data)
        navLogService.processNavData(departurePort, arrivalPort, data)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping(
        value = ["/navlog"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAllNavData(): ResponseEntity<List<NavigationLog>> {
        LOG.info("Fetching all navigation log data")
        val data = navLogService.getNavLogs()
        return ResponseEntity.ok(data)
    }

    @GetMapping(
        value = ["/navlog/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getNavData(@PathVariable("id") id: Long): ResponseEntity<NavigationLog> {
        LOG.info("Fetching navigation log for id: $id")
        val data = navLogService.getNavLogFor(id)
        return ResponseEntity.ok(data)
    }

    @DeleteMapping(
        value = ["/navlog/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun deleteNavData(@PathVariable("id") id: Long): ResponseEntity<Unit> {
        LOG.info("Deleting navigation log for id: $id")
        navLogService.deleteNavLogFor(id)
        LOG.info("Successfully deleted navigation log for id: $id")
        return ResponseEntity.ok().build()
    }
}