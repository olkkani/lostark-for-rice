package io.oikkani.integrationservice.application.port.inbound

fun interface AuctionFetchUseCase {
    suspend fun fetchAndSendPriceData()
}