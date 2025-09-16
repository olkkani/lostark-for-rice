package io.oikkani.processorservice.application.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.oikkani.processorservice.application.port.inbound.AuctionSnapshotUseCase
import io.oikkani.processorservice.infrastructure.config.repository.PostgresqlTestContainersConfig
import io.oikkani.processorservice.infrastructure.config.security.TestSecurityConfig
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
@Import(PostgresqlTestContainersConfig::class, TestSecurityConfig::class)
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
                    val savedTodayOhlcPrices = jpaRepository.findAllByItemCode(1000)
                    savedTodayOhlcPrices shouldHaveSize 1
                    savedTodayOhlcPrices.first().openPrice shouldBe 1000
                    savedTodayOhlcPrices.first().highPrice shouldBe 10000
                    savedTodayOhlcPrices.first().lowPrice shouldBe 1000
                    savedTodayOhlcPrices.first().closePrice shouldBe 1000
                }
            }
            context("오늘의 OHLC Price 가 이미 존재하는 경우") {
                service.saveSnapshotAndUpdateHlcPrice(snapshot)
                val savedTodayOhlcPrices = jpaRepository.findAllByItemCode(1000)

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
                val savedNextTimeOhlcPrices = jpaRepository.findAllByItemCode(1000)

                it("new ohlc price is created") {
                    savedTodayOhlcPrices shouldHaveSize 1
                    savedTodayOhlcPrices.first().openPrice shouldBe 1000
                    savedTodayOhlcPrices.first().highPrice shouldBe 10000
                    savedTodayOhlcPrices.first().lowPrice shouldBe 1000
                    savedTodayOhlcPrices.first().closePrice shouldBe 1000
                }
                it("snapshot data eq 15") {
                    val savedSnapshot = snapshotJpaRepository.findAllByItemCode(itemCode)
                    savedSnapshot shouldHaveSize 15
                }
                it("hlc price is updated") {
                    val updatedOhlcPrice = jpaRepository.findAllByItemCode(itemCode)
                    updatedOhlcPrice shouldHaveSize 1
                    updatedOhlcPrice.first().openPrice shouldBe 1000
                    updatedOhlcPrice.first().highPrice shouldBe 11000
                    updatedOhlcPrice.first().lowPrice shouldBe 500
                    updatedOhlcPrice.first().closePrice shouldBe 500
                }
            }
        }
    }
}