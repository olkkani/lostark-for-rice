package io.oikkani.lfr.service

import io.oikkani.lfr.model.AuctionRequest
import io.oikkani.lfr.model.AuctionResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

//@Value("\${api_key}")
private var apiKey: String = "api_key=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyIsImtpZCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyJ9.eyJpc3MiOiJodHRwczovL2x1ZHkuZ2FtZS5vbnN0b3ZlLmNvbSIsImF1ZCI6Imh0dHBzOi8vbHVkeS5nYW1lLm9uc3RvdmUuY29tL3Jlc291cmNlcyIsImNsaWVudF9pZCI6IjEwMDAwMDAwMDAwNjU1NTIifQ.jETZnd9myHKT8mWNfzF_8G6wwho4Vpf3ajqbTtSA-CYr5crd-6PA5eRZPbWc39xCXFtYr4OGWViJ18zB_MrHg7Ml2VkSDQ16I_sXqb5gPc4EVtrkat55uY8tX3uEzkL_WiT6cbeojWq7P-JO5yxPwX64L7B3PBHM58fBU-d3GghPpv1ffj59nlW3mGUlrNS9jdu5w9RDiBW8R-0fg-bxK09dpB7reX3SY4Q0gDYQCz1NGMNg_DQ1EHlsKWwe0Vff3ntDGX9PaY6_Lxqv3O3wQVX0xg26gzrGqoz53yH3Vg1Z_zoCPB_GZjlzo2mIwv-Kk57CnrzqadIAewu7f_IuVg"

@Service
class AuctionClientService {
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
   val service = AuctionClientService()
    service.getAuctionItems(lv10jem)

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
