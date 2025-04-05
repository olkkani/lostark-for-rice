package io.olkkani.lfr.repository.jpa

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.olkkani.lfr.entity.jpa.AuctionPriceIndex
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@DataJpaTest
@ActiveProfiles("test")
class AuctionPriceRepoTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var repository: AuctionPriceIndexRepo

    init {
        this.describe("Transactional Update") {
            context("가져온 값의 Close Price 를 2000으로 변경하면") {
                it("저장하지 않고 새롭게 값을 가져왔을 때 값이 변경되어있음") {
                    val itemCode = 1
                    val today = LocalDate.now()

                    val priceIndex = AuctionPriceIndex(
                        itemCode = itemCode,
                        recordedDate = today,
                        openPrice = 1000,
                        lowPrice = 1000,
                        highPrice = 2000,
                        closePrice = 1000,
                    )
                    repository.save(priceIndex)

                    val savedPriceIndex =
                        repository.findByItemCodeAndRecordedDate(itemCode = itemCode, recordedDate = today)
                    if (savedPriceIndex != null) {
                        savedPriceIndex.closePrice = 2000
                    }

                    val changedPriceIndex =
                        repository.findByItemCodeAndRecordedDate(itemCode = itemCode, recordedDate = today)
                    if (changedPriceIndex != null) {

                        changedPriceIndex.openPrice shouldBe 1000
                        changedPriceIndex.closePrice shouldBe 2000
                    }
                }
            }
        }
        this.describe("Null Check Test and Update") {
            context("DB에서 조건에 맞지않는 값을 가져올 경우") {
                val today = LocalDate.now()
                val itemCode = 1
                val savedPriceIndex =
                    repository.findByItemCodeAndRecordedDate(itemCode = itemCode, recordedDate = today)
                it("값이 존재하지 않음") {
                    savedPriceIndex shouldBe null
                }
                it("값이 존재하지 않아 생성된 값은 DB에 존재") {
                    var createdPriceIndex: AuctionPriceIndex? = null
                    if (savedPriceIndex == null) {
                        repository.save(
                            AuctionPriceIndex(
                                itemCode = itemCode, recordedDate = today,
                                closePrice = 1000,
                                openPrice = 1000,
                                highPrice = 1000,
                                lowPrice = 1000
                            )
                        )
                        createdPriceIndex =
                            repository.findByItemCodeAndRecordedDate(itemCode = itemCode, recordedDate = today)
                    }
                    createdPriceIndex shouldNotBe null
                }
            }
        }
    }
}