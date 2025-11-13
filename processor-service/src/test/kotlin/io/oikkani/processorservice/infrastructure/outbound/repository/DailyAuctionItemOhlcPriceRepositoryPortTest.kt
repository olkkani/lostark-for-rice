package io.oikkani.processorservice.infrastructure.outbound.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.oikkani.processorservice.application.port.outbound.AuctionItemOhlcPriceRepositoryPort
import io.oikkani.processorservice.infrastructure.config.repository.PostgresqlTestContainersConfig
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyAuctionItemOhlcPriceEntity
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.DailyAuctionItemOhlcPriceJpaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
@Import(PostgresqlTestContainersConfig::class)
class DailyAuctionItemOhlcPriceRepositoryPortTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var auctionRepository: AuctionItemOhlcPriceRepositoryPort
    @Autowired
    private lateinit var jpaRepository: DailyAuctionItemOhlcPriceJpaRepository


    init {
        val today = LocalDate.now()

        describe("get-all-today-ohlc-prices") {
            context("today-get-ohlc-price") {
                val ohlcPrices = mutableListOf<DailyAuctionItemOhlcPriceEntity>()

                // list add today ohlc price
                for(i in 1..5) {
                    ohlcPrices.add(
                        DailyAuctionItemOhlcPriceEntity(
                            itemCode = 1000*i,
                            recordedDate = today,
                            openPrice = 1000*i,
                            highPrice = 1000*i,
                            lowPrice = 1000*i,
                            closePrice = 1000*i
                        )
                    )
                }

                // list add not today ohlc price
                for (i in 1..5) {
                    ohlcPrices.add(
                        DailyAuctionItemOhlcPriceEntity(
                            itemCode = 1000*i,
                            recordedDate = today.minusDays(i.toLong()),
                            openPrice = 1000*i,
                            highPrice = 1000*i,
                            lowPrice = 1000*i,
                            closePrice = 1000*i
                        )
                    )
                }
                jpaRepository.saveAll(ohlcPrices)

                it("get_total_count_5") {
                    auctionRepository.getAllTodayItems() shouldHaveSize 5
                }
            }
        }
    }
}