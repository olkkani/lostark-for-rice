package io.olkkani.lfr.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.floats.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.olkkani.lfr.adapter.external.dao.MarketDAO
import io.olkkani.lfr.config.PostgresqlTestContainersConfig
import io.olkkani.lfr.repository.DailyMarketItemOhlcaPriceRepo
import io.olkkani.lfr.repository.MarketItemPriceSnapshotRepo
import io.olkkani.lfr.repository.entity.DailyMarketItemOhlcaPrice
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
@Import(PostgresqlTestContainersConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LostarkMarketSchedulerTest(
) : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)
    @Autowired
    private lateinit var scheduler: LostarkMarketScheduler
    @Autowired
    private lateinit var snapshotRepo: MarketItemPriceSnapshotRepo
    @Autowired
    private lateinit var dailyMarketItemOhlcaPriceRepo: DailyMarketItemOhlcaPriceRepo

    init {
        xdescribe("Lostark Market Scheduler Test") {
            context("유물 각인서 가져올 경우") {
                scheduler.fetchEngravingRecipePriceAndUpdatePrice()
                it("유물 각인서 개수가 10 이상") {
                    snapshotRepo.findAll().size shouldBeGreaterThan 10
                }
            }

        }
        describe("Update Today HLC Price Test"){
            context(""){

                it(""){

                }
            }
        }
        xdescribe("전날 가격 업데이트 테스트"){
            val abidos = MarketDAO(
                categoryCode = 50010,
                itemCode = 6861012,
                itemName = "아비도스 융화 재료"
            )
            val requestItemCode: Int = abidos.itemCode!!
            val yesterday = LocalDate.now().minusDays(1)
            // 어제자 데이터 업데이트
            dailyMarketItemOhlcaPriceRepo.save(
                DailyMarketItemOhlcaPrice(
                    itemCode = requestItemCode,
                    recordedDate = yesterday,
                    openPrice = 1000,
                    highPrice = 1000,
                    lowPrice = 1000,
                    closePrice = 1000
                )
            )
            context("데이터를 가져와서 업데이트하면"){
                scheduler.fetchMaterialPriceAndUpdatePrice()
                it("어제 평균가가 0과 같음"){
                    val yesterdayOhlcaPrice = dailyMarketItemOhlcaPriceRepo.findByItemCodeAndRecordedDate(itemCode = requestItemCode, recordedDate = yesterday)
                    yesterdayOhlcaPrice.shouldNotBeNull()
                    yesterdayOhlcaPrice.avgPrice shouldBe 0F
                }
            }
           context("데이터를 가져와서 어제자 평균값을 업데이트하면"){
               scheduler.fetchMaterialPriceAndUpdatePrice(true)
               it("어제 평균가가 0 보다 큼"){
                   val yesterdayOhlcaPrice = dailyMarketItemOhlcaPriceRepo.findByItemCodeAndRecordedDate(itemCode = requestItemCode, recordedDate = yesterday)
                   yesterdayOhlcaPrice.shouldNotBeNull()
                   yesterdayOhlcaPrice.avgPrice shouldBeGreaterThan 0F
               }
           }
        }
    }
}