package io.oikkani.processorservice.application.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.oikkani.processorservice.application.port.inbound.AuctionSnapshotUseCase
import io.oikkani.processorservice.infrastructure.config.repository.PostgresqlTestContainersConfig
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyAuctionItemOhlcPriceEntity
import io.oikkani.processorservice.infrastructure.outbound.repository.jooq.DailyAuctionItemOhlcPriceJooqRepository
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.AuctionItemPriceSnapshotJpaRepository
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.DailyAuctionItemOhlcPriceJpaRepository
import io.olkkani.common.dto.contract.AuctionPrice
import io.olkkani.common.dto.contract.AuctionPriceSnapshot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
@Import(PostgresqlTestContainersConfig::class)
class AuctionSnapshotServiceTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var service: AuctionSnapshotUseCase

    @Autowired
    private lateinit var jpaRepository: DailyAuctionItemOhlcPriceJpaRepository

    @Autowired
    private lateinit var snapshotJpaRepository: AuctionItemPriceSnapshotJpaRepository

    @Autowired
    private lateinit var jooqRepository: DailyAuctionItemOhlcPriceJooqRepository

    init {
        val now = LocalDateTime.now()
        val today = now.toLocalDate()
        val itemCode = 1000
        describe("auction-snapshot-service-test") {
            beforeContainer {
                service.deleteAll()
            }


            // snapshot date. lowPrice = 1_000, howPrice = 10_000
            val prices = mutableListOf<AuctionPrice>()
            for (i in 1..10) {
                prices.add(
                    AuctionPrice(
                        price = 1000 * i,
                        endDate = now
                    )
                )
            }
            val snapshot = AuctionPriceSnapshot(
                itemCode = 1000,
                prices = prices
            )


            context("오늘의 OHLC Price 가 없는 경우") {
                service.saveSnapshotAndUpdateHlcPrice(snapshot)
                it("saved-new-ohlc-price") {
                    val savedTodayOhlcPrices = jpaRepository.findAllByRecordedDate(today)
                    val savedTodayOhlcPrice: DailyAuctionItemOhlcPriceEntity? = savedTodayOhlcPrices.find { it.itemCode == 1000 }

                    savedTodayOhlcPrices shouldHaveSize 1

                    savedTodayOhlcPrice.shouldNotBeNull()
                    savedTodayOhlcPrice.let {
                        it.itemCode shouldBe 1000
                        it.openPrice shouldBe 1000
                        it.highPrice shouldBe 10000
                        it.lowPrice shouldBe 1000
                        it.closePrice shouldBe 1000
                        it.recordedDate shouldBe today
                    }
                }
            }
            context("오늘의 OHLC Price 가 이미 존재하는 경우") {
                service.saveSnapshotAndUpdateHlcPrice(snapshot)
                    val savedPrevOhlcPrices = jpaRepository.findAllByRecordedDate(today)

                val nextTimePriceSnapshot = AuctionPriceSnapshot(
                    itemCode = 1000,
                    prices = listOf(
                        // duplication price
                        AuctionPrice(price = 3000, endDate = now),
                        AuctionPrice(price = 3000, endDate = now),
                        AuctionPrice(price = 4000, endDate = now),
                        AuctionPrice(price = 5000, endDate = now),
                        AuctionPrice(price = 6000, endDate = now),
                        // new price
                        AuctionPrice(price = 500, endDate = now),
                        AuctionPrice(price = 1000, endDate = now.plusMinutes(1)),
                        AuctionPrice(price = 1100, endDate = now.plusMinutes(1)),
                        AuctionPrice(price = 2400, endDate = now.plusMinutes(1)),
                        AuctionPrice(price = 11000, endDate = now.plusMinutes(1)),
                    )
                )
                service.saveSnapshotAndUpdateHlcPrice(nextTimePriceSnapshot)
                val savedNextTimeOhlcPrices = jpaRepository.findAllByRecordedDate(today)

                it("new ohlc price is created") {
                    savedPrevOhlcPrices.size.shouldBe(1)
                    val savedPrevOhlcPrice = savedPrevOhlcPrices.find { it.itemCode == 1000 }

                    savedPrevOhlcPrices.size.shouldBe(1)
                    savedPrevOhlcPrice.shouldNotBeNull()
                    savedPrevOhlcPrice.let {
                        it.itemCode shouldBe 1000
                        it.openPrice shouldBe 1000
                        it.highPrice shouldBe 10000
                        it.lowPrice shouldBe 1000
                        it.closePrice shouldBe 1000
                        it.recordedDate shouldBe today
                    }
                }
                it("snapshot data eq 15") {
                    val savedSnapshot = snapshotJpaRepository.findAllByItemCode(itemCode)
                    savedSnapshot shouldHaveSize 15
                }
                it("hlc price is updated") {
                    savedNextTimeOhlcPrices shouldHaveSize 1
                    val savedNextTimeOhlcPrice = savedNextTimeOhlcPrices.find { it.itemCode == 1000 }
                    savedNextTimeOhlcPrice.shouldNotBeNull()

                    savedNextTimeOhlcPrice.let {
                        it.openPrice.shouldBe(1000)
                        it.highPrice.shouldBe(11000)
                        it.lowPrice.shouldBe(500)
                        it.closePrice.shouldBe(500)
                        it.recordedDate.shouldBe(today)
                    }
                }
            }
        }
    }
}