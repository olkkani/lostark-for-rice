package io.oikkani.integrationservice.application.service

import io.oikkani.integrationservice.application.port.inbound.MarketFetchMaterialUseCase
import io.oikkani.integrationservice.domain.dto.MarketItemCondition
import io.oikkani.integrationservice.infrastructure.outbound.client.lostark.MarketClient
import io.oikkani.integrationservice.infrastructure.outbound.client.processor.ProcessorMarketClient
import org.springframework.stereotype.Service

@Service
class MarketFetchMaterialService(
    private val marketClient: MarketClient,
    private val processorMarketClient: ProcessorMarketClient,
) : MarketFetchMaterialUseCase {


    override suspend fun fetchAndSendPriceData() {
        val abidos = MarketItemCondition(
            categoryCode = 50010,
            itemCode = 6861012,
            itemName = "아비도스 융화 재료"
        )

        val response = marketClient.fetchItems(abidos.toFusionMaterialRequest())
        response?.let {
            processorMarketClient.sendMarketPriceData(it.toDomain())
        }
    }

}