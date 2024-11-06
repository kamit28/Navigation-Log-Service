package com.assignment.navlog.exception

import com.assignment.navlog.model.MessageDTO
import jakarta.validation.ConstraintViolationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpServerErrorException.InternalServerError


@RestControllerAdvice
@ResponseBody
class NavLogExceptionHandler {
    @Autowired
    private val msgSource: MessageSource? = null

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun constraintViolationException(e: ConstraintViolationException): ResponseEntity<String> {
        return ResponseEntity("Validation Error: " + e.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun processValidationError(ex: MethodArgumentNotValidException): MessageDTO? {
        val result = ex.bindingResult
        val error = result.fieldError

        return processFieldError(error)
    }

    @ExceptionHandler(InternalServerError::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun internalServerError(ex: InternalServerError): ResponseEntity<MessageDTO> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(MessageDTO(MessageDTO.MessageType.ERROR, ex.message ?: ""))
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFoundError(ex: ResourceNotFoundException): MessageDTO {
        return MessageDTO(MessageDTO.MessageType.ERROR, ex.message ?: "")
    }

    private fun processFieldError(error: FieldError?): MessageDTO? {
        var message: MessageDTO? = null
        if (error != null) {
            val currentLocale = LocaleContextHolder.getLocale()
            val msg = msgSource!!.getMessage(error.defaultMessage!!, null, currentLocale)
            message = MessageDTO(MessageDTO.MessageType.ERROR, msg)
        }
        return message
    }
}