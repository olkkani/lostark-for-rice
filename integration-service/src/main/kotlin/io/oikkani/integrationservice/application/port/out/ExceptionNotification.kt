package io.oikkani.integrationservice.application.port.out

fun interface ExceptionNotification {
    fun sendErrorNotification(errorMessage: String, actionName: String)
}