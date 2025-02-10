package io.olkkani.lfr.repository.mongo

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.olkkani.lfr.LostarkForRiceApplication
import io.olkkani.lfr.entity.mongo.TodayItemPrice
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime

@ActiveProfiles("test")
@DataMongoTest
@ContextConfiguration(classes = [LostarkForRiceApplication::class])
class TodayItemPriceRepositoryTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var repository: TodayItemPriceRepository


    init {
        this.describe("데이터 중복 방지 삽입 테스트") {
            context("10개의 값이 이미 존재하는 상태에서 중복되는 값 5개 새로운 값 5개를 추가하면") {
                val todayPrices = mutableListOf<TodayItemPrice>()
                val fixedTime = LocalDateTime.of(2025, 1, 1, 10, 10, 10)
                val itemCode = 1
                // 기존 데이터 추가
                for (i in 1..5) {
                    todayPrices.add(
                        TodayItemPrice(
                            itemCode = itemCode,
                            endDate = fixedTime.plusSeconds(i.toLong()),
                            price = i * 1000,
                        )
                    )
                    todayPrices.add(
                        TodayItemPrice(
                            itemCode = itemCode + 1,
                            endDate = fixedTime.plusSeconds(i.toLong()),
                            price = i * 1000,
                        )
                    )
                }
                repository.saveAll(todayPrices)
                todayPrices.clear()
                // 중복된 값과 새로운 값을 포함하여 삽입
                for (i in 1..5) {
                    todayPrices.add(
                        TodayItemPrice(
                            itemCode = itemCode,
                            endDate = fixedTime.plusSeconds(i.toLong()),
                            price = i * 2000,
                        )
                    )
                    todayPrices.add(
                        TodayItemPrice(
                            itemCode = itemCode + 2,
                            endDate = fixedTime.plusSeconds(i.toLong()),
                            price = i * 1000,
                        )
                    )
                }
                repository.saveIfNotExists(todayPrices)
                it("현재 존재하는 값의 개수가 15개가 된다.") {
                    val savedTodayPrice = repository.findAll()
                    savedTodayPrice.size shouldBe 15
                }
            }
            context("전체 삭제하기를 하면") {
                repository.deleteAll()
                it("현재 존재하는 값의 개수가 0개가 된다.") {
                    repository.findAll().size shouldBe 0
                }
            }
        }
        this.describe("특정 itemCode로 조회 테스트"){
            context("itemCode가 10인 todayPrices 를 가져오면"){
                val todayPrices = mutableListOf<TodayItemPrice>()
                val fixedTime = LocalDateTime.of(2025, 1, 1, 10, 10, 10)
                val itemCode = 10
                // 기존 데이터 추가
                for (i in 1..5) {
                    todayPrices.add(
                        TodayItemPrice(
                            itemCode = itemCode,
                            endDate = fixedTime.plusSeconds(i.toLong()),
                            price = i * 1000,
                        )
                    )
                    todayPrices.add(
                        TodayItemPrice(
                            itemCode = itemCode + 1,
                            endDate = fixedTime.plusSeconds(i.toLong()),
                            price = i * 1000,
                        )
                    )
                }
                repository.saveIfNotExists(todayPrices)
                val savedItemPrices = repository.findPricesByItemCode(itemCode = itemCode).map{ it.getPrice()}
                it("prices 의 개수는 5개 이다."){
                    savedItemPrices.size shouldBe 5

                }
                it("prices의 개수는 5개이며 각 항목의 type은 Int 이다."){
                    savedItemPrices.first().shouldBeInstanceOf<Int>()

                }
            }
        }
    }
}