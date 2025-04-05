package io.olkkani.lfr.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.olkkani.lfr.dao.GemDAO
import io.olkkani.lfr.entity.jpa.AuctionPriceIndex
import io.olkkani.lfr.repository.jpa.AuctionPriceIndexRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
class SchedulerJobTest() : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)
    @Autowired
    private lateinit var priceService: GemPriceService
    @Autowired
    private lateinit var repository: AuctionPriceIndexRepo
    @Autowired
    private lateinit var schedulerService: LostarkAuctionScheduler

    init {
        this.describe("price index trend test") {
            val today = LocalDate.now()
            val sampleIndex = mutableListOf<AuctionPriceIndex>()
            val gemDAO = listOf(
            GemDAO(itemCode = 65021100, pairItemCode = 65022100, name = "10레벨 멸화의 보석"),
            GemDAO(itemCode = 65022100, pairItemCode = 65021100, name = "10레벨 홍염의 보석"),
            GemDAO(itemCode = 65031080, pairItemCode = 65032080, name = "8레벨 겁화의 보석"),
            GemDAO(itemCode = 65032080, pairItemCode = 65031080, name = "8레벨 작열의 보석"),
            GemDAO(itemCode = 65031100, pairItemCode = 65032100, name = "10레벨 겁화의 보석"),
            GemDAO(itemCode = 65032100, pairItemCode = 65031100, name = "10레벨 작열의 보석")
        )
            // 예제 데이터 삽입
            gemDAO.forEach { gem ->
                for (i in 0..1) {
                    sampleIndex.add(
                        AuctionPriceIndex(
                            itemCode = gem.itemCode,
                            closePrice = 1000 * (i + 1),
                            recordedDate = today.minusDays(i.toLong()),
                            openPrice = 1000,
                            highPrice = 1000,
                            lowPrice = 1000,
                        )
                    )
                }
            }
            repository.saveAll(sampleIndex)
            context("오늘의 가격차를 계산한 결과를 호출하면") {
                schedulerService.calculateGapTodayItemPrice()
                it("어제와의 가격차는 -1000, 짝 보석과의 가격차는 0") {
                    val itemCode = 65021100
                    val savedPriceTrend = priceService.getPrevTenDaysIndexTrendByItemCode(itemCode)
                    val todayPriceRecord = savedPriceTrend.priceRecords.find { it.date == today }
                    todayPriceRecord?.prevGapPrice shouldBe -1000
                    todayPriceRecord?.pairGapPrice shouldBe 0
                }
            }
        }
    }
}