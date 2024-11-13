package io.oikkani.lfr

import io.ktor.http.ContentType
import io.oikkani.lfr.model.AuctionRequest
import io.oikkani.lfr.model.AuctionResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux


class LostArkApiClient(
    private val apiKey: String
) {
    private val webClient = WebClient.builder()
        .baseUrl("https://developer-lostark.game.onstove.com")
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "bearer $apiKey")
        .build()

    fun getAuctionItems(request: AuctionRequest): Flux<AuctionResponse> {
        return webClient.post()
            .uri("/auctions/items")
            .bodyValue(request)
            .retrieve()
            .bodyToFlux(AuctionResponse::class.java)
            .onErrorResume { error ->
                println("Error occurred: ${error.message}")
                Flux.empty()
            }
    }
}

// 사용 예시
fun main() {
    val apiKey = ""
    val client = LostArkApiClient(apiKey)

    val request = AuctionRequest(
        sort = "BUY_PRICE",
        categoryCode = 210000,
        itemTier = 3,
        itemGrade = "유물",
        itemName = "10레벨 멸화의 보석",
        pageNo = 0,
        sortCondition = "ASC"
    )

    client.getAuctionItems(request)
        .subscribe(
            { response ->
                println("Total Count: ${response.totalCount}")
                response.items.forEach { item ->
                    println("Item Name: ${item.auctionInfo.buyPrice}")
                }
            },
            { error ->
                println("Error: ${error.message}")
            },
            {
                println("Completed")
            }
        )

    // Flux가 비동기로 동작하므로, 메인 스레드가 종료되지 않도록 대기
    Thread.sleep(5000)
}