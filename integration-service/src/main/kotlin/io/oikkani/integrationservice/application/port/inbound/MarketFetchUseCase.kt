package io.oikkani.integrationservice.application.port.inbound

fun interface MarketFetchUseCase {
    suspend fun fetchAndSendPriceData()
}