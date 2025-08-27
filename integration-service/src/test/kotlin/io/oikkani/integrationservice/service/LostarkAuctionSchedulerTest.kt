package io.oikkani.integrationservice.service

import com.github.f4b6a3.tsid.TsidCreator
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import io.olkkani.lfr.adapter.external.LostarkAPIClient
import io.olkkani.lfr.adapter.external.dto.AuctionInfo
import io.olkkani.lfr.adapter.external.dto.AuctionItem
import io.olkkani.lfr.adapter.external.dto.AuctionResponse
import io.olkkani.lfr.config.PostgresqlTestContainersConfig
import io.olkkani.lfr.config.TestSecurityConfig
import io.olkkani.lfr.repository.AuctionItemPriceSnapshotRepo
import io.olkkani.lfr.repository.DailyAuctionItemOhlcPriceRepo
import io.olkkani.lfr.repository.ItemPreviousPriceChangeRepo
import io.olkkani.lfr.repository.entity.AuctionItemPriceSnapshot
import io.olkkani.lfr.repository.entity.DailyAuctionItemOhlcPrice
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
@Import(PostgresqlTestContainersConfig::class, TestSecurityConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LostarkAuctionSchedulerTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var ohlcPriceRepo: DailyAuctionItemOhlcPriceRepo

    @Autowired
    private lateinit var todayPriceRepo: AuctionItemPriceSnapshotRepo

    @Autowired
    private lateinit var itemPreviousPriceChangeRepo: ItemPreviousPriceChangeRepo

    @Autowired
    private lateinit var scheduler: LostarkAuctionScheduler

    @Autowired
    @Qualifier("mockAuctionAPIClient")
    private lateinit var mockApiClient: LostarkAPIClient

    init {

        describe("updateTodayHlcPrice 메서드") {
            val itemCode = 65021100
            val testDate = LocalDate.now()

            beforeContainer {
                // 테스트 전 데이터 정리
                ohlcPriceRepo.deleteAll()
                todayPriceRepo.truncateTable()
            }

            context("오늘의 OHLC 데이터가 없을 때") {
                // 가격 스냅샷 데이터 준비
                val priceSnapshots = listOf(
                    AuctionItemPriceSnapshot(
                        id = TsidCreator.getTsid().toLong(),
                        itemCode = itemCode,
                        endDate = LocalDateTime.now(),
                        price = 5000
                    ),
                    AuctionItemPriceSnapshot(
                        id = TsidCreator.getTsid().toLong(),
                        itemCode = itemCode,
                        endDate = LocalDateTime.now(),
                        price = 10000
                    ),
                    AuctionItemPriceSnapshot(
                        id = TsidCreator.getTsid().toLong(),
                        itemCode = itemCode,
                        endDate = LocalDateTime.now(),
                        price = 7500
                    )
                )

                todayPriceRepo.saveAllIgnoreDuplicates(priceSnapshots)

                it("새로운 OHLC 데이터가 생성되어야 함") {
                    val currentPrice = 8000

                    scheduler.updateTodayHlcPrice(itemCode, currentPrice)

                    val ohlcData = ohlcPriceRepo.findByItemCodeAndRecordedDate(itemCode, testDate)
                    ohlcData.shouldNotBeNull()
                    ohlcData.itemCode shouldBe itemCode
                    ohlcData.recordedDate shouldBe testDate
                    ohlcData.closePrice shouldBe currentPrice
                    ohlcData.highPrice shouldBe 10000 // 최고가
                    ohlcData.lowPrice shouldBe 5000   // 최저가
                    ohlcData.openPrice shouldBe 5000  // 시가는 최저가로 설정
                }
            }

            context("오늘의 OHLC 데이터가 이미 있을 때") {
                val existingOhlc = DailyAuctionItemOhlcPrice(
                    itemCode = itemCode,
                    recordedDate = testDate,
                    openPrice = 6000,
                    highPrice = 9000,
                    lowPrice = 5500,
                    closePrice = 7000
                )
                ohlcPriceRepo.save(existingOhlc)

                // 새로운 가격 스냅샷 추가
                val newSnapshot = AuctionItemPriceSnapshot(
                    id = TsidCreator.getTsid().toLong(),
                    itemCode = itemCode,
                    endDate = LocalDateTime.now(),
                    price = 12000 // 새로운 최고가
                )
                todayPriceRepo.saveAllIgnoreDuplicates(listOf(newSnapshot))

                it("기존 OHLC 데이터가 업데이트되어야 함") {
                    val newCurrentPrice = 11000

                    scheduler.updateTodayHlcPrice(itemCode, newCurrentPrice)

                    val updatedOhlc = ohlcPriceRepo.findByItemCodeAndRecordedDate(itemCode, testDate)
                    updatedOhlc.shouldNotBeNull()
                    updatedOhlc.closePrice shouldBe newCurrentPrice
                    updatedOhlc.highPrice shouldBe 12000 // 업데이트된 최고가
                    updatedOhlc.openPrice shouldBe 6000  // 시가는 변경되지 않음
                }
            }
        }

        describe("calculateGapTodayItemPrice 메서드") {
            val itemCode1 = 65021100 // 10레벨 멸화의 보석
            val itemCode2 = 65022100 // 10레벨 홍염의 보석 (pair item)
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)

            beforeContainer {
                // 테스트 데이터 정리
                itemPreviousPriceChangeRepo.truncateTable()
                ohlcPriceRepo.deleteAll()
            }

            context("오늘과 어제의 OHLC 데이터, 그리고 페어 아이템 데이터가 모두 있을 때") {
                // 테스트 데이터 준비
                val yesterdayOhlc = DailyAuctionItemOhlcPrice(
                    itemCode = itemCode1,
                    recordedDate = yesterday,
                    openPrice = 10000,
                    highPrice = 11000,
                    lowPrice = 9000,
                    closePrice = 10500
                )

                val todayOhlc = DailyAuctionItemOhlcPrice(
                    itemCode = itemCode1,
                    recordedDate = today,
                    openPrice = 10500,
                    highPrice = 12000,
                    lowPrice = 10000,
                    closePrice = 11500
                )

                val pairTodayOhlc = DailyAuctionItemOhlcPrice(
                    itemCode = itemCode2,
                    recordedDate = today,
                    openPrice = 9000,
                    highPrice = 10000,
                    lowPrice = 8500,
                    closePrice = 9500
                )

                ohlcPriceRepo.saveAll(listOf(yesterdayOhlc, todayOhlc, pairTodayOhlc))

                it("가격 변화 데이터가 올바르게 계산되어야 함") {
                    scheduler.calculateGapTodayItemPrice()

                    val priceChange = itemPreviousPriceChangeRepo.findByItemCodeAndRecordedDate(itemCode1, today)
                    priceChange.shouldNotBeNull()
                    priceChange.itemCode shouldBe itemCode1
                    priceChange.price shouldBe 11500
                    priceChange.priceDiffPrevDay shouldBe 1000 // 11500 - 10500
                    priceChange.priceDiffPairItem shouldBe 2000 // 11500 - 9500
                    // 비율 계산 검증
                    priceChange.priceDiffRatePrevDay shouldNotBe 0.0
                    priceChange.priceDiffRatePairItem shouldNotBe 0.0
                }
            }
        }

    }

    private fun createMockAuctionResponse(): AuctionResponse {
        return AuctionResponse(
            items = listOf(
                AuctionItem(
                    auctionInfo = AuctionInfo(
                        buyPrice = 10000,
                        endDate = LocalDateTime.now().plusHours(1)
                    )
                ),
                AuctionItem(
                    auctionInfo = AuctionInfo(
                        buyPrice = 12000,
                        endDate = LocalDateTime.now().plusHours(2)
                    )
                )
            )
        )
    }

    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        @Qualifier("mockAuctionAPIClient")
        fun mockAuctionApiClient(): LostarkAPIClient {
            return mockk<LostarkAPIClient>(relaxed = true)
        }
    }
}