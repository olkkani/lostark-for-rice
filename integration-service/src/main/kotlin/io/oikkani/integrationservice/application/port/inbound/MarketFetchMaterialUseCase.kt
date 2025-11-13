package io.oikkani.integrationservice.application.port.inbound

fun interface MarketFetchMaterialUseCase {
    suspend fun fetchAndSendPriceData()
}