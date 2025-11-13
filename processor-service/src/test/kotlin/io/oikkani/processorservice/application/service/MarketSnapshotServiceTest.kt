package io.oikkani.processorservice.application.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.oikkani.processorservice.application.port.inbound.MarketSnapshotUseCase
import io.oikkani.processorservice.application.port.outbound.MarketItemOhlcaRepositoryPort
import io.oikkani.processorservice.infrastructure.config.repository.PostgresqlTestContainersConfig
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.DailyMarketItemOhlcaPriceJpaRepository
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.MarketItemPriceSnapshotJpaRepository
import io.olkkani.common.dto.contract.MarketPrice
import io.olkkani.common.dto.contract.MarketPriceSnapshot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
@Import(PostgresqlTestContainersConfig::class)
class MarketSnapshotServiceTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var service: MarketSnapshotUseCase

    @Autowired
    private lateinit var snapshotJpaRepository: MarketItemPriceSnapshotJpaRepository

    @Autowired
    private lateinit var ohlcJpaRepository: DailyMarketItemOhlcaPriceJpaRepository

    @Autowired
    private lateinit var ohlcaRepository: MarketItemOhlcaRepositoryPort

    init {

        describe("market-snapshot-service-test") {
            val today = LocalDate.now()

            context("오늘의 Ohlca price 가 없는 경우") {
                val marketPrices = listOf<MarketPrice>(
                    MarketPrice(itemCode = 1000, price = 1000),
                    MarketPrice(itemCode = 2000, price = 2000),
                    MarketPrice(itemCode = 3000, price = 3000),
                    MarketPrice(itemCode = 4000, price = 4000),
                    MarketPrice(itemCode = 5000, price = 5000),
                )
                val marketPriceSnapshot = MarketPriceSnapshot(prices = marketPrices)
                service.saveSnapshotAndUpdateHlcaPrice(marketPriceSnapshot)
                it("save new ohlc price") { val savedOhlcPrices = ohlcaRepository.findAllByRecordedDate(today)
                    savedOhlcPrices shouldHaveSize 5
                }
            }
            context("이미 존재하는 상태에서 낮은 금액을 추가하면") {
                val marketPrices = listOf<MarketPrice>(
                    MarketPrice(itemCode = 1000, price = 500),
                    MarketPrice(itemCode = 2000, price = 2000),
                    MarketPrice(itemCode = 3000, price = 3000),
                    MarketPrice(itemCode = 4000, price = 4000),
                    MarketPrice(itemCode = 5000, price = 5000),
                )
                val marketPriceSnapshot = MarketPriceSnapshot(prices = marketPrices)
                service.saveSnapshotAndUpdateHlcaPrice(marketPriceSnapshot)
                it("최저가 갱신") {
                    val savedOhlcPrices = ohlcaRepository.findAllByRecordedDate(today)
                    savedOhlcPrices shouldHaveSize 5
                    val ohlcPrice = savedOhlcPrices.find { it.itemCode == 1000 }
                    ohlcPrice shouldNotBe null
                    ohlcPrice!!.openPrice shouldBe 1000
                    ohlcPrice.highPrice shouldBe 1000
                    ohlcPrice.lowPrice shouldBe 500
                    ohlcPrice.closePrice shouldBe 500
                }
            }
            context("이미 존재하는 상황에서 높은 금액을 추가하면") {
                val marketPrices = listOf<MarketPrice>(
                    MarketPrice(itemCode = 1000, price = 2000),
                    MarketPrice(itemCode = 2000, price = 2000),
                    MarketPrice(itemCode = 3000, price = 3000),
                    MarketPrice(itemCode = 4000, price = 4000),
                    MarketPrice(itemCode = 5000, price = 5000),
                )
                val marketPriceSnapshot = MarketPriceSnapshot(prices = marketPrices)
                service.saveSnapshotAndUpdateHlcaPrice(marketPriceSnapshot)
                it("최고가 갱신") {
                    val savedOhlcPrices = ohlcaRepository.findAllByRecordedDate(today)
                    savedOhlcPrices shouldHaveSize 5
                    val ohlcPrice = savedOhlcPrices.find { it.itemCode == 1000 }
                    ohlcPrice shouldNotBe null
                    ohlcPrice!!.openPrice shouldBe 1000
                    ohlcPrice.highPrice shouldBe 2000
                    ohlcPrice.lowPrice shouldBe 500
                    ohlcPrice.closePrice shouldBe 2000
                }
            }
        }
    }
}

