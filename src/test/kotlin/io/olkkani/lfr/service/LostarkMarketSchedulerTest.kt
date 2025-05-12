package io.olkkani.lfr.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.olkkani.lfr.dto.Items
import io.olkkani.lfr.dto.MarketResponse
import io.olkkani.lfr.repository.jpa.MarketPriceIndexRepo
import io.olkkani.lfr.repository.mongo.MarketPriceProjection
import io.olkkani.lfr.repository.mongo.MarketTodayPriceMongoRepo
import io.olkkani.lfr.util.LostarkAPIClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
class LostarkMarketSchedulerTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)
    private val apiClient = mockk<LostarkAPIClient>()
    private val todayPriceRepo = mockk<MarketTodayPriceMongoRepo>(relaxed = true)
    @Autowired
    private lateinit var indexRepo: MarketPriceIndexRepo

    init {
        describe("MarketSchedulerTest") {
            val marketScheduler = spyk(
                LostarkMarketScheduler(
                    indexRepo = indexRepo,
                    todayPriceRepo = todayPriceRepo,
                    apiClient = apiClient
                )
            )
            this.describe("TodayPrice Update Test") {
                val itemCode = 6861012
                coEvery {
                    apiClient.fetchMarketItemPriceSubscribe(any())
                } returns MarketResponse(
                    items = listOf(
                        Items(
                            id = 6861012,
                            currentMinPrice = 88,
                            yDayAvgPrice = 83F
                        )
                    )
                )
                every { todayPriceRepo.saveIfNotExists(any()) } returns Unit
                every { todayPriceRepo.findPricesByItemCode(itemCode) } returns listOf(
                    object : MarketPriceProjection {
                        override fun getPrice(): Int = 85
                    },
                    object : MarketPriceProjection {
                        override fun getPrice(): Int = 88
                    },
                    object : MarketPriceProjection {
                        override fun getPrice(): Int = 90
                    }
                )
                context("데이터를 불러오고 오늘자 index를 수정하면") {
                    marketScheduler.fetchPriceAndUpdatePrice(isUpdateYesterdayAvgPrice = false)

                    it("현재 close_price 값이 88") {
                        val todayIndex =
                            indexRepo.findByItemCodeAndRecordedDate(itemCode = itemCode, recordedDate = LocalDate.now())

                        todayIndex.shouldNotBeNull()
                        todayIndex.closePrice shouldBe 88
                    }
                }
            }
        }
    }
}