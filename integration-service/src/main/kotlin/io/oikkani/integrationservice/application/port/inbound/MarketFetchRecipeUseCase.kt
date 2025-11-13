package io.oikkani.integrationservice.application.port.inbound

fun interface MarketFetchRecipeUseCase {
    suspend fun fetchAndSendPriceData()
}