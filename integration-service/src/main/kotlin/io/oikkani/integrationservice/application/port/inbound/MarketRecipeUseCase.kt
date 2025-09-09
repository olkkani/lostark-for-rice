package io.oikkani.integrationservice.application.port.inbound



fun interface MarketRecipeUseCase {
    suspend fun fetchAndSendPriceDate(isUpdateYesterdayAvgPrice: Boolean)

}