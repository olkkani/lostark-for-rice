package io.olkkani.lfr.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.olkkani.lfr.config.QueryDslConfiguration
import io.olkkani.lfr.entity.ItemPrices
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@DataJpaTest
@Import(QueryDslConfiguration::class, ItemPricesRepositorySupport::class)
@ActiveProfiles("test")
class ItemPriceRepositorySupportTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var repository: ItemPricesRepository
    @Autowired
    private lateinit var repositorySupport: ItemPricesRepositorySupport

    init {
        this.describe("findPrevFiveDaysPricesByItemCode") {
            context("어제부터 5일 전까지의 값을 가져오도록 호출하면") {
                it("가져온 갯수가 5개가 된다.") {
                    val itemCode = 123456
                    val itemPrices: MutableList<ItemPrices> = mutableListOf()
                    for (i in 0..10) {
                        val price = i * 1000
                        itemPrices.add(
                            ItemPrices(
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
                    val result = repositorySupport.findPrevEightDaysPricesByItemCode(itemCode)
                    result.size shouldBe 5
                }
            }
        }
    }
}