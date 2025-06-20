package io.olkkani.lfr.util

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.floats.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import io.olkkani.lfr.adapter.external.LostarkAPIClient
import io.olkkani.lfr.adapter.external.dao.MarketDAO
import io.olkkani.lfr.common.util.ExceptionNotification
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class LostarkAPIClientTest(
) : DescribeSpec() {
    val exceptionNotification = mockk<ExceptionNotification>(relaxed = true)
    private val apiKey = System.getenv("AUCTION_API_KEY") ?: "must_not_empty_key"

    val apiClient = LostarkAPIClient(apiKey, exceptionNotification)

    private val logger = KotlinLogging.logger {}

    init {
        describe("Lostark API Market Test") {
            context("abidos 아이템을 가져오면") {
                val request = MarketDAO(
                    categoryCode = 50010,
                    itemCode = 6861012,
                    itemName = "아비도스 융화 재료"
                )

                it("현재 가격과 전일 평균가가 0 이상") {
                    runBlocking {
                        val response =
                            apiClient.fetchMarketItemPriceSubscribe(request.toFusionMaterialRequest())?.items[0]
                        if (apiKey != "must_not_empty_key") {
                            response shouldNotBe null
                            response!!.id shouldBe 6861012
                            response.yDayAvgPrice shouldBeGreaterThan 0F
                        }
                    }
                }
            }
            context("유물 각인서 목록을 가져오면") {
                val request = MarketDAO(
                    categoryCode = 40000,
                    itemGrade = "유물"
                ).toRelicEngravingRecipeRequest(1)

                logger.info { request.toString() }
                it("제대로 가져옴") {

                    runBlocking {
                        val response = apiClient.fetchMarketItemPriceSubscribe(request)
                        logger.info { response.toString() }
                        if (apiKey != "must_not_empty_key") {
                            response shouldNotBe null
                            response!!.items.size shouldBe 10
                        }
                    }
                }
            }
        }
    }
}