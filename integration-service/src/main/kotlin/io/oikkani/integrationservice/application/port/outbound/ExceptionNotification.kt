package io.oikkani.integrationservice.application.port.outbound

import io.oikkani.integrationservice.domain.dto.AlertError

fun interface ExceptionNotification {
     fun sendErrorNotification(alertError: AlertError)
}