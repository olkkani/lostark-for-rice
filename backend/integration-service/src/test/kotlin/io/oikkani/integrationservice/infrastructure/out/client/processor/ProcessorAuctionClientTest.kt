package io.oikkani.integrationservice.infrastructure.out.client.processor

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.olkkani.common.dto.contract.AuctionItemPrice
import io.olkkani.common.dto.contract.AuctionPrice
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@ActiveProfiles("test")
class ProcessorAuctionClientTest: DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    val serviceDomain = "http://localhost:80"
    val client: WebClient = WebClient.builder()
        .baseUrl(serviceDomain)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()

    fun createAuctionItemPrice(): AuctionItemPrice {
        return AuctionItemPrice(
            itemCode = 1,
            prices = listOf(
                AuctionPrice(1, LocalDateTime.now()),
                AuctionPrice(2, LocalDateTime.now()),
                AuctionPrice(3, LocalDateTime.now()),
                AuctionPrice(4, LocalDateTime.now()),
                AuctionPrice(5, LocalDateTime.now()),
            )
        )
    }

    init {
        xdescribe("ProcessorAuctionClient Test"){
            context("save-auction-items-snapshots"){
                val request = createAuctionItemPrice()
                val returnStateCode =  client.post()
                    .uri("auction/items/snapshots")
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .awaitSingle()
                it("no content"){
                    returnStateCode.statusCode.is2xxSuccessful shouldBe true
                }
            }
        }
    }


}