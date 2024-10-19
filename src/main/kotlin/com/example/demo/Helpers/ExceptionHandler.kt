package com.example.demo.Helpers

import java.lang.Exception
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.example.demo.Helpers.MissingFieldException
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<String> {
        return ResponseEntity("Resource not found: ${ex.message}", HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity("Invalid argument: ${ex.message}", HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGenericException(ex: Exception): ResponseEntity<String> {
        return ResponseEntity("An unexpected error occurred: ${ex.message}", HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(MissingFieldException::class)
    fun handleMissingFieldException(ex: MissingFieldException): ResponseEntity<String> {
        return ResponseEntity("Missing required field: ${ex.message}", HttpStatus.BAD_REQUEST)
    }
}