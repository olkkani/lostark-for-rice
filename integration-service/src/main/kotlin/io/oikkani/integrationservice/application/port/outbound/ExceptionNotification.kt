package io.oikkani.integrationservice.application.port.outbound

import io.oikkani.integrationservice.domain.dto.AlertErrorDTO

fun interface ExceptionNotification {
     fun sendErrorNotification(alertError: AlertErrorDTO)
}