package io.oikkani.integrationservice.application.service

import io.oikkani.integrationservice.application.port.inbound.MarketFetchRecipeUseCase
import io.oikkani.integrationservice.domain.dto.MarketItemCondition
import io.oikkani.integrationservice.infrastructure.outbound.client.lostark.MarketClient
import io.oikkani.integrationservice.infrastructure.outbound.client.processor.ProcessorMarketClient
import io.olkkani.common.dto.contract.MarketPrice
import org.springframework.stereotype.Service

@Service
class MarketFetchRecipeService(
    private val apiClient: MarketClient,
    private val processorMarketClient: ProcessorMarketClient,
) : MarketFetchRecipeUseCase {

    val itemCondition = MarketItemCondition(
        categoryCode = 40000,
        itemGrade = "유물"
    )

    override suspend fun fetchAndSendPriceData() {
        //TODO 비동기, 논블로킹, 병렬 처리로 변경
        val prices = mutableListOf<MarketPrice>()
        (1..5).map { // 실제 각인수 개수에 따른 페이지
            val response = apiClient.fetchItems(itemCondition.toRelicEngravingRecipeRequest(it))
            response?.let { responseMarketPrices ->
                prices.addAll(responseMarketPrices.toDomain())
            }
        }
        processorMarketClient.sendMarketPriceData(prices)
    }
}