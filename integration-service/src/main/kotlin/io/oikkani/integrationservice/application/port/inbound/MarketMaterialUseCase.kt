package io.oikkani.integrationservice.application.port.inbound

fun interface MarketMaterialUseCase {
    suspend fun fetchAndSendPriceData(isUpdateOpenPriceAndYesterdayAvgPrice: Boolean)
}