package io.oikkani.integrationservice.application.port.inbound

fun interface AuctionGemUseCase {
    suspend fun fetchAndSendPriceData()
}