package io.oikkani.integrationservice.domain.dto

class AlertErrorDTO (
    val actionName: String,
    val retryAttempts: Int? = 0,
    val errorCode: Int,
    val errorStatus: String,
    val errorMessage: String,
)