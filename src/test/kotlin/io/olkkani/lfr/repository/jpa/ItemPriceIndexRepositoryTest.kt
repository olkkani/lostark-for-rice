package io.olkkani.lfr.repository.jpa

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.olkkani.lfr.entity.jpa.ItemPriceIndex
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@ActiveProfiles("test")
@DataJpaTest
class ItemPriceIndexRepositoryTest: DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var repository: ItemPriceIndexRepository

    init {
        @Transactional
        this.describe("Transactional Update"){
            context("가져온 값의 Close Price 를 2000으로 변경하면"){
                it("저장하지 않고 새롭게 값을 가져왔을 때 값이 변경되어있음"){
                    val itemCode = 1
                    val today = LocalDate.now()

                    val priceIndex = ItemPriceIndex(
                        itemCode = itemCode,
                        recordedDate = today,
                        openPrice = 1000,
                        lowPrice = 1000,
                        highPrice = 2000,
                        closePrice = 1000,
                    )
                    repository.save(priceIndex)

                    val savedPriceIndex = repository.findByItemCodeAndRecordedDate(itemCode = itemCode, recordedDate = today)
                    savedPriceIndex.closePrice = 2000

                    val changedPriceIndex = repository.findByItemCodeAndRecordedDate(itemCode = itemCode, recordedDate = today)
                    changedPriceIndex.openPrice shouldBe 1000
                    changedPriceIndex.closePrice shouldBe 2000
                }
            }
        }
    }
}