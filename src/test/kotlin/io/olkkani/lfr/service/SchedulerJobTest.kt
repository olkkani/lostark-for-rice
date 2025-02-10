package io.olkkani.lfr.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.olkkani.lfr.dto.collectGemInfoList
import io.olkkani.lfr.entity.jpa.ItemPriceIndex
import io.olkkani.lfr.repository.jpa.ItemPriceIndexRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
class SchedulerJobTest: DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var schedulerService: AuctionSchedulerService
    @Autowired
    private lateinit var priceService: GemPriceService
    @Autowired
    lateinit var repository: ItemPriceIndexRepository

    init {
        this.describe("price index trend test"){
            val today = LocalDate.now()
            val sampleIndex = mutableListOf<ItemPriceIndex>()
            // 예제 데이터 삽입
            collectGemInfoList.forEach { gemInfo ->
                for(i in 0 .. 1){
                    sampleIndex.add(ItemPriceIndex(
                        itemCode = gemInfo.itemCode,
                        closePrice = 1000 * (i + 1),
                        recordedDate = today.minusDays(i.toLong()),
                        openPrice = 1000,
                        highPrice = 1000,
                        lowPrice = 1000,
                    ))
                }
            }
            repository.saveAll(sampleIndex)
            context("오늘의 가격차를 계산한 결과를 호출하면"){
               schedulerService.calculateGapTodayItemPrice()
                it("어제와의 가격차는 -1000, 짝 보석과의 가격차는 0"){
                    val itemCode = 65021100
                    val savedPriceTrend = priceService.getPrevTenDaysIndexTrendByItemCode(itemCode)
                    val todayPriceRecord = savedPriceTrend.priceRecords.find{it.date == today}
                    todayPriceRecord?.prevGepPrice shouldBe -1000
                    todayPriceRecord?.pairGapPrice shouldBe 0
                }
            }
        }
    }
}