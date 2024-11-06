package com.assignment.navlog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration

@SpringBootApplication
@Configuration
class NavlogServiceApplication

fun main(args: Array<String>) {
	runApplication<NavlogServiceApplication>(*args)
}
