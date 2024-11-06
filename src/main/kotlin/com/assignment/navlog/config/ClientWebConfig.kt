package com.assignment.navlog.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@ComponentScan(basePackages = ["com.assignment.navlog"])
class ClientWebConfig : WebMvcConfigurer {
    @Bean
    fun methodValidationPostProcessor(): MethodValidationPostProcessor {
        return MethodValidationPostProcessor()
    }
}