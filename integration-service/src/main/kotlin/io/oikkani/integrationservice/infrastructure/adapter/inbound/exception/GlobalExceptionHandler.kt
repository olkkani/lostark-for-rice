package io.oikkani.integrationservice.infrastructure.adapter.inbound.exception

import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import io.oikkani.integrationservice.domain.dto.AlertError
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler(
    private val exceptionNotification: ExceptionNotification,
) {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(CustomException::class)
    fun handleCommonException(e: CustomException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            e.exceptionCode.status.value(),
            e.exceptionCode.message,
        )

        if(e.exceptionCode.status.is5xxServerError) {
            val alertError = AlertError(
                actionName = "Request_Error",
                errorCode = e.exceptionCode.hashCode(),
                errorStatus = e.exceptionCode.message,
                errorMessage = e.stackTraceToString(),
            )

            exceptionNotification.sendErrorNotification(alertError)
        }

        return ResponseEntity(errorResponse, e.exceptionCode.status)
    }
}