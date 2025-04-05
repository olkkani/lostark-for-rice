package io.olkkani.lfr.repository.jpa

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.olkkani.lfr.entity.jpa.AuctionPriceIndex
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@DataJpaTest
@ActiveProfiles("test")
class ItemPriceRepositoryTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var repository: AuctionPriceIndexRepo

    init {
        this.describe("findPrevFiveDaysPricesByItemCode") {
            context("어제부터 7일 전까지의 값을 가져오도록 호출하면") {
                val itemCode = 123456
                val itemPrices: MutableList<AuctionPriceIndex> = mutableListOf()
                for (i in 0..10) {
                    val price = i * 1000
                    itemPrices.add(
                        AuctionPriceIndex(
                            id = i.toLong(),
                            closePrice = price,
                            openPrice = price,
                            highPrice = price,
                            lowPrice = price,
                            itemCode = itemCode,
                            recordedDate = LocalDate.now().minusDays(i.toLong())
                        )
                    )
                }
                repository.saveAll(itemPrices)
                val result = repository.findPrevSixDaysPricesByItemCode(itemCode)
                it("가져온 갯수가 6개가 된다.") {
                    result.size shouldBe 6
                }
            }
        }
    }
}