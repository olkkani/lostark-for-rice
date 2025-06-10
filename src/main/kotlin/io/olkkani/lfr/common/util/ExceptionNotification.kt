package io.olkkani.lfr.common.util

fun interface ExceptionNotification {
    fun sendErrorNotification(errorMessage: String, actionName: String)
}