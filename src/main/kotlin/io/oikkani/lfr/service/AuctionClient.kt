package io.oikkani.lfr.service

import io.oikkani.lfr.model.AuctionRequest
import io.oikkani.lfr.model.AuctionResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

//@Value("\${api_key}")
private var apiKey: String = ""

class AuctionClient {
    private val baseUrl: String = "https://developer-lostark.game.onstove.com"
    private val client: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "barer $apiKey")
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()

    fun getAuctionItems(auctionRequest: AuctionRequest): Flux<AuctionResponse> {
        return client.post()
            .uri("/auctions/items")
            .bodyValue(auctionRequest)
            .retrieve()
            .bodyToFlux(AuctionResponse::class.java)
            .onErrorResume { error ->
                println("Error occurred: ${error.message}")
                Flux.empty()
            }
    }




}


fun main() {
    val lv10jem: AuctionRequest = AuctionRequest(
        itemTier = 3,
        itemGrade = "유물",
        itemName = "10레벨 멸화의 보석",
        pageNo = 0
    )

    println(lv10jem)

}

//    fun getAuctionItems(auctionRequest: AuctionRequest): Flux<AuctionItem> {
//        return Flux.create{
//            emitter ->
//            runBlocking{
//
//                val response = client.post(baseUrl){
//                    headers {
//                        append(HttpHeaders.Accept, "application/json")
//                        append(HttpHeaders.Authorization, "Bearer $apiKey")
//                        append(HttpHeaders.ContentType, "application/json")
//                    }
//                    setBody(auctionRequest)
//                }

//                re

//                val auctionItems = Json.decodeFromString<List<AuctionItem>>(response)
//                auctionItems.forEach { emitter.next(it) }
//                emitter.complete()
//            }
//        }
//    }

//    suspend inline fun getAuction(){
//        val client = HttpClient(CIO)
//        client.post()
//    }
