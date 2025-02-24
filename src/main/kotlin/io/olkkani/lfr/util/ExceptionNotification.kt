package io.olkkani.lfr.util

fun interface ExceptionNotification {
    fun sendErrorNotification(errorMessage: String, actionName: String)
}