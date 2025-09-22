package io.oikkani.integrationservice.application.service

import io.oikkani.integrationservice.application.port.inbound.MarketFetchMaterialUseCase
import io.oikkani.integrationservice.domain.dto.MarketItemCondition
import io.oikkani.integrationservice.infrastructure.outbound.client.lostark.MarketClient
import io.oikkani.integrationservice.infrastructure.outbound.client.processor.ProcessorMarketClient
import io.olkkani.common.dto.contract.MarketPrice
import org.springframework.stereotype.Service

@Service
class MarketFetchRecipeService(
    private val apiClient: MarketClient,
    private val processorMarketClient: ProcessorMarketClient,
) : MarketFetchMaterialUseCase {

    val itemCondition = MarketItemCondition(
        categoryCode = 40000,
        itemGrade = "유물"
    )

    override suspend fun fetchAndSendPriceData() {
        //TODO 비동기, 논블로킹, 병렬 처리로 변경
        val prices = mutableListOf<MarketPrice>()
        (1..5).map {
            val response = apiClient.fetchItems(itemCondition.toRelicEngravingRecipeRequest(it))
            response?.let { responseMarketPrices ->
                prices.addAll(responseMarketPrices.toDomain())
            }
        }
        processorMarketClient.sendMarketPriceData(prices)
    }
}