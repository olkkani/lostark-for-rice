package io.olkkani.lfr.service

fun interface ExceptionNotification {
    fun sendErrorNotification(errorMessage: String, actionName: String)
}