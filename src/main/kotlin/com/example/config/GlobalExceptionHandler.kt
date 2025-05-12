package com.example.config

import com.example.domain.auth.exception.AuthException
import com.example.domain.common.exception.InvalidRequestException
import com.example.domain.common.exception.ServerException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(InvalidRequestException::class)
    fun handleInvalidRequest(ex: InvalidRequestException): ResponseEntity<ErrorResponse> {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.message)
    }

    @ExceptionHandler(AuthException::class)
    fun handleAuthException(ex: AuthException): ResponseEntity<ErrorResponse> {
        return errorResponse(HttpStatus.UNAUTHORIZED, ex.message)
    }

    @ExceptionHandler(ServerException::class)
    fun handleServerException(ex: ServerException): ResponseEntity<ErrorResponse> {
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.message)
    }

    private fun errorResponse(status: HttpStatus, message: String?): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                status = status.name,
                code = status.value(),
                message = message ?: "예기치 않은 오류가 발생했습니다."
            ),
            status
        )
    }

    data class ErrorResponse(
        val status: String,
        val code: Int,
        val message: String
    )
}
