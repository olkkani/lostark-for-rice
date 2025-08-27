// infrastructure/adapter/out/client/AuctionClient.kt
package io.oikkani.integrationservice.infrastructure.adapter.out.client

import io.oikkani.integrationservice.application.port.out.ExceptionNotification
import io.oikkani.integrationservice.external.dto.AuctionResponse
import io.oikkani.integrationservice.infrastructure.adapter.out.client.dto.request.AuctionRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Component
class AuctionClient(
    @param:Value("\${lostark.auction.api.key:must-not-null-auction-apikey}") apiKey: String,
    exceptionNotification: ExceptionNotification,
) : BaseLostarkClient(exceptionNotification) {

    private val baseUrl: String = "https://developer-lostark.game.onstove.com"

    val client: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "bearer $apiKey")
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate") // 압축 요청
        .clientConnector(createHttpConnector()) // 압축 지원 커넥터
        .build()

    private fun createHttpConnector(): ReactorClientHttpConnector {
        val httpClient = HttpClient.create()
            .compress(true) // 요청/응답 압축 활성화
            .responseTimeout(Duration.ofSeconds(30))
            .keepAlive(true)
        
        return ReactorClientHttpConnector(httpClient)
    }




    private fun fetchAuctionItems(auctionRequest: AuctionRequest): Mono<AuctionResponse> {
        return client.post()
            .uri("/auctions/items")
            .bodyValue(auctionRequest)
            .retrieve()
            .bodyToMono(AuctionResponse::class.java)
            .onErrorResume { error ->
                handleError(error, "fetch_auction_items_error")
            }
    }

    /**
     * 경매장 아이템 조회 (공개 메서드)
     */
    suspend fun fetchAuctionItemsAsync(auctionRequest: AuctionRequest): AuctionResponse {
        return subscribeSingle(fetchAuctionItems(auctionRequest))
    }
}