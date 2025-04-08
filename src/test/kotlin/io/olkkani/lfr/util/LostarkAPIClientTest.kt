package io.olkkani.lfr.util

import io.kotest.common.runBlocking
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import io.olkkani.lfr.dto.MarketRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class LostarkAPIClientTest(
    @Value("\${lostark.api.key:must_not_empty_key}") private val apiKey: String
) : DescribeSpec() {

    init {
        this.describe("Lostark API Market Test") {
            val abidos: MarketRequest = MarketRequest(50010, "아비도스 융화 재료")
            val exceptionNotification = mockk<ExceptionNotification>(relaxed = true)
            val apiClient = LostarkAPIClient(apiKey, exceptionNotification)
            context("abidos 아이템을 가져오면") {

                it("현재 가격과 전일 평균가가 0 이상") {
                    runBlocking {
                        val response = apiClient.fetchMarketItemPriceSubscribe(abidos)?.items[0]
                        if (apiKey != "must_not_empty_key") {
                            response shouldNotBe null
                            response!!.id shouldBe 6861012
                        }
                    }
                }
            }
        }
    }
}