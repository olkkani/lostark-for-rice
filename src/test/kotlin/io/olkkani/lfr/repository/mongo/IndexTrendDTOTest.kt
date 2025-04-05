package io.olkkani.lfr.repository.mongo

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.olkkani.lfr.entity.mongo.RecentPriceIndexTrend
import io.olkkani.lfr.entity.mongo.PriceRecord
import io.olkkani.lfr.entity.mongo.toResponse
import java.time.LocalDate

class IndexTrendDTOTest : DescribeSpec() {

    init {
        this.describe("DTOSortTest"){
            context("무작위 정렬로 생성된 날짜 데이터를 정렬하면"){
                var indexTrend: RecentPriceIndexTrend = RecentPriceIndexTrend(
                    itemCode = 1
                )

                val numbers = (0..10).toMutableList()
                numbers.shuffle()

                numbers.forEach { number ->
                    indexTrend.priceRecords.add(PriceRecord(
                        date = LocalDate.now().minusDays(number.toLong()),
                        price = number,
                        prevGapPrice = number,
                        prevGapPriceRate = number.toDouble(),
                        pairGapPrice = number,
                        pairGapPriceRate = number.toDouble(),
                    ))

                }
                val priceRecords = indexTrend.toResponse()
                it("첫 행은 가장 최근의 날짜가 출력"){
                    priceRecords.first().date shouldBe LocalDate.now()
                }
            }
        }
    }
}