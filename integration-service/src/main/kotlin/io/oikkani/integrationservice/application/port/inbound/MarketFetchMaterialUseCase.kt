package io.oikkani.integrationservice.application.port.inbound

interface MarketFetchMaterialUseCase {
    suspend fun fetchAndSendPriceData(isUpdateYesterdayAvgPrice: Boolean = false)
}