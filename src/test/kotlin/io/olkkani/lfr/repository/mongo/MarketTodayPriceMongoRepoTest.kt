package io.olkkani.lfr.repository.mongo

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.olkkani.lfr.entity.mongo.MarketTodayPrice
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@DataMongoTest
class MarketTodayPriceMongoRepoTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var repository: MarketTodayPriceMongoRepo

    init {
        describe("기존 항목이 10개인 상태에서") {
            val todayPrices = mutableListOf<MarketTodayPrice>()
            val itemCode = 1

            for (i in 1..10) {
                todayPrices.add(
                    MarketTodayPrice(
                        itemCode = itemCode,
                        price = i * 1000
                    )
                )
            }
            repository.saveAll(todayPrices)
            todayPrices.clear()
            context("중복 데이터 5개, 값이 다른 데이터가 15개를 추가한 경우") {

                // 값이 다른 데이터 5, 같은 데이터 5
                for (i in 1..5) {
                    todayPrices.add(
                        MarketTodayPrice(
                            itemCode = itemCode,
                            price = i * 100
                        )
                    )
                    todayPrices.add(
                        MarketTodayPrice(
                            itemCode = itemCode,
                            price = i * 1000
                        )
                    )
                    todayPrices.add(
                        MarketTodayPrice(
                            itemCode = itemCode + i,
                            price = i * 100
                        )
                    )
                    todayPrices.add(
                        MarketTodayPrice(
                            itemCode = itemCode + i,
                            price = i * 1000
                        )
                    )
                }
                todayPrices.forEach { repository.saveIfNotExists(it) }
                it("전체 개수는 25") {
                    val savedAllTodayPrices = repository.findAll().map { it.price }
                    savedAllTodayPrices.size shouldBe 25
                }
                it("itemCode 가 1인 항목이 15") {
                    val savedTodayPrices = repository.findPricesByItemCode(itemCode = itemCode).map { it.getPrice() }
                    savedTodayPrices.size shouldBe 15
                }
            }
        }
    }
}