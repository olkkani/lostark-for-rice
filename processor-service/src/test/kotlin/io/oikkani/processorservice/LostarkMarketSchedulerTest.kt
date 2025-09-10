//package io.oikkani.integrationservice.service
//
//import io.kotest.core.spec.style.DescribeSpec
//import io.kotest.extensions.spring.SpringExtension
//import io.kotest.matchers.ints.shouldBeGreaterThan
//import io.kotest.matchers.nulls.shouldNotBeNull
//import io.kotest.matchers.shouldBe
//import io.mockk.coEvery
//import io.mockk.mockk
//import io.mockk.slot
//import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.lostark.dto.request.MarketRequest
//import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.lostark.dto.response.Item
//import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.lostark.dto.response.MarketResponse
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.context.TestConfiguration
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Import
//import org.springframework.context.annotation.Primary
//import org.springframework.test.context.ActiveProfiles
//import java.time.LocalDate
//
//@SpringBootTest
//@ActiveProfiles("test")
////@Import(PostgresqlTestContainersConfig::class, TestSecurityConfig::class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class LostarkMarketSchedulerTest(
//) : DescribeSpec() {
//    override fun extensions() = listOf(SpringExtension)
//
////    @Autowired
////    private lateinit var scheduler: LostarkMarketScheduler
////    @Autowired
////    private lateinit var snapshotRepo: MarketItemPriceSnapshotRepo
////    @Autowired
////    private lateinit var dailyMarketItemOhlcaPriceRepo: DailyMarketItemOhlcaPriceRepo
//
////    @Autowired
////    @Qualifier("marketAPIClient")
////    private lateinit var mockApiClient: LostarkAPIClient
//
//    init {
//        xdescribe("Lostark Market Scheduler Test") {
//            context("유물 각인서 가져올 경우") {
//                scheduler.fetchEngravingRecipePriceAndUpdatePrice()
//                it("유물 각인서 개수가 10 이상") {
//                    snapshotRepo.findAll().size shouldBeGreaterThan 10
//                }
//            }
//
//        }
//        xdescribe("전날 가격 업데이트 테스트") {
//            val abidos = MarketDAO(
//                categoryCode = 50010,
//                itemCode = 6861012,
//                itemName = "아비도스 융화 재료"
//            )
//            val requestItemCode: Int = abidos.itemCode!!
//            val yesterday = LocalDate.now().minusDays(1)
//            val requestSlot = slot<MarketRequest>()
//
//            beforeContainer {
//                // 각 테스트 전에 데이터 정리
//                dailyMarketItemOhlcaPriceRepo.deleteAll()
//                dailyMarketItemOhlcaPriceRepo.flush()
//            }
//
//
//            coEvery {
//                mockApiClient.fetchMarketItemPriceSubscribe(capture(requestSlot))
//            } returns createMockMarketResponse()
//
//
//            context("데이터를 가져와서 업데이트하면") {
//                val testYesterdayData = createTestData(requestItemCode, yesterday)
//                dailyMarketItemOhlcaPriceRepo.saveAndFlush(testYesterdayData)
//                dailyMarketItemOhlcaPriceRepo.findAll().size shouldBe 1
//
//                scheduler.fetchMaterialPriceAndUpdatePrice()
//                it("어제 평균가가 0과 같음") {
//                    val yesterdayOhlcaPrice = dailyMarketItemOhlcaPriceRepo.findByItemCodeAndRecordedDate(
//                        itemCode = requestItemCode,
//                        recordedDate = yesterday
//                    )
//                    yesterdayOhlcaPrice.shouldNotBeNull()
//                    yesterdayOhlcaPrice.avgPrice shouldBe 0F
//                    dailyMarketItemOhlcaPriceRepo.findAll().size shouldBe 2
//                }
//            }
//            context("데이터를 가져와서 어제자 평균값을 업데이트하면") {
//                val testYesterdayData = createTestData(requestItemCode, yesterday)
//                dailyMarketItemOhlcaPriceRepo.saveAndFlush(testYesterdayData)
//                dailyMarketItemOhlcaPriceRepo.findAll().size shouldBe 1
//
//                scheduler.fetchMaterialPriceAndUpdatePrice(true)
//                it("어제 평균가가 0 보다 큼") {
//                    val yesterdayOhlcaPrice = dailyMarketItemOhlcaPriceRepo.findByItemCodeAndRecordedDate(
//                        itemCode = requestItemCode,
//                        recordedDate = yesterday
//                    )
//                    yesterdayOhlcaPrice.shouldNotBeNull()
//                    yesterdayOhlcaPrice.avgPrice shouldBe 10F
//                    dailyMarketItemOhlcaPriceRepo.findAll().size shouldBe 2
//                }
//            }
//        }
//        xdescribe("어제 평균가 가격 업데이트 테스트"){
//            beforeContainer {
//                dailyMarketItemOhlcaPriceRepo.deleteAll()
//            }
//            context("어제자 가격을 89.9 로 업데이트 하면"){
//                val itemCode = 6861012
//                val yesterday = LocalDate.now().minusDays(1)
//                val yesterdayAvgPrice: Float = 89.9F
//
//                // Save Yesterday test ohlc price
//                val testYesterdayData = createTestData(itemCode, yesterday)
//                dailyMarketItemOhlcaPriceRepo.saveAndFlush(testYesterdayData)
//                dailyMarketItemOhlcaPriceRepo.findAll().size shouldBe 1
//
//
//                dailyMarketItemOhlcaPriceRepo.findByItemCodeAndRecordedDate(itemCode = itemCode, recordedDate = yesterday)?.apply {
//                    avgPrice = yesterdayAvgPrice
//                    dailyMarketItemOhlcaPriceRepo.save(this)
//                }
//                it("어제자 평균가는 89.9"){
//                    dailyMarketItemOhlcaPriceRepo.findByItemCodeAndRecordedDate(itemCode = itemCode, recordedDate = yesterday)?.avgPrice shouldBe yesterdayAvgPrice
//                }
//
//            }
//
//
//        }
//    }
//
//    @TestConfiguration
//    class TestConfig {
//        @Bean
//        @Primary
//        @Qualifier("marketAPIClient")
//        fun mockMarketApiClient(): LostarkAPIClient {
//            return mockk<LostarkAPIClient>(relaxed = true)
//        }
//    }
//    private fun createTestData(requestItemCode: Int, yesterday: LocalDate): DailyMarketItemOhlcaPrice {
//        return DailyMarketItemOhlcaPrice(
//            itemCode = requestItemCode,
//            recordedDate = yesterday,
//            openPrice = 1000,
//            highPrice = 1000,
//            lowPrice = 1000,
//            closePrice = 1000
//        )
//    }
//
//    private fun createMockMarketResponse(): MarketResponse {
//        return MarketResponse(
//            items = listOf(
//                Item(
//                    id = 6861012,
//                    currentMinPrice = 1000,
//                    yDayAvgPrice = 10F,
//                )
//            ),
//        )
//    }
//}