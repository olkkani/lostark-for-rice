package io.oikkani.integrationservice.application.port.inbound

interface MarketFetchRecipeUseCase {
    suspend fun fetchAndSendPriceData(isUpdateYesterdayAvgPrice: Boolean = false)
}