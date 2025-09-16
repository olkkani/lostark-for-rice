package io.oikkani.integrationservice.application.service

import io.oikkani.integrationservice.application.port.inbound.MarketRecipeUseCase
import io.oikkani.integrationservice.domain.dto.MarketItemCondition
import io.oikkani.integrationservice.infrastructure.outbound.client.lostark.MarketClient
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class MarketRecipeService(
    private val apiClient: MarketClient,
) : MarketRecipeUseCase {


    val relicEngravingRecipe = MarketItemCondition(
        categoryCode = 40000,
        itemGrade = "유물"
    )


    override suspend fun fetchAndSendPriceDate() = coroutineScope {
        var pageNo = 1
//        val items = mutableListOf<MarketResponse>()
//        while (true) {
//            async {
//                val request = relicEngravingRecipe.toRelicEngravingRecipeRequest(pageNo++)
//                apiClient.fetchItemsAsync(request)?.let { response ->
//                    items.add(response)
//                }
//            }
//        }




    }
}