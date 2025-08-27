// infrastructure/adapter/out/client/MarketClient.kt
package io.oikkani.integrationservice.infrastructure.adapter.out.client

import io.oikkani.integrationservice.application.port.out.ExceptionNotification
import io.oikkani.integrationservice.external.dto.MarketRequest
import io.oikkani.integrationservice.external.dto.MarketResponse
import reactor.core.publisher.Mono

class MarketClient(
    apiKey: String,
    exceptionNotification: ExceptionNotification,
) : BaseLostarkClient(apiKey, exceptionNotification) {

    /**
     * 마켓 아이템 조회 (내부 메서드)
     */
    private fun fetchMarketItem(marketRequest: MarketRequest): Mono<MarketResponse> {
        return client.post()
            .uri("/markets/items")
            .bodyValue(marketRequest)
            .retrieve()
            .bodyToMono(MarketResponse::class.java)
            .onErrorResume { error ->
                handleError(error, "fetch_market_item_error")
            }
    }

    /**
     * 마켓 아이템 가격 조회 (공개 메서드)
     */
    suspend fun fetchMarketItemPriceAsync(marketRequest: MarketRequest): MarketResponse? {
        return subscribeSingleOrNull(fetchMarketItem(marketRequest))
    }
}