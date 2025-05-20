package io.olkkani.lfr.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.olkkani.lfr.config.TestContainersConfig
import io.olkkani.lfr.entity.MarketItemPriceSnapshot
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainersConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MarketItemPriceSnapshotRepoTest(
    private val marketItemPriceSnapshotRepo: MarketItemPriceSnapshotRepo
) : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    init {
        xdescribe("중복 없는 단건 저장 테스트") {
            context("중복없는 단건 4건 저장시") {
                for (i in 1..2) {
                    for (j in 3..4) {
                        marketItemPriceSnapshotRepo.saveIgnoreDuplicates(
                            MarketItemPriceSnapshot(
                                itemCode = i * 1000,
                                price = j * 1000
                            )
                        )
                    }
                }
                it("저장된 건수가 4건") {
                    val savedItems = marketItemPriceSnapshotRepo.findAll()
                    savedItems.isNotEmpty() shouldBe true
                    savedItems.size shouldBe 4
                }
            }
            context("노중복4건 저장 후 중복 2건을 저장하는 경우") {
                for (i in 1..2) {
                    for (j in 3..4) {
                        marketItemPriceSnapshotRepo.saveIgnoreDuplicates(
                            MarketItemPriceSnapshot(
                                itemCode = i * 1000,
                                price = j * 1000
                            )
                        )
                    }
                }
                marketItemPriceSnapshotRepo.saveIgnoreDuplicates(
                    MarketItemPriceSnapshot(itemCode = 1000, price = 3000)
                )
                marketItemPriceSnapshotRepo.saveIgnoreDuplicates(
                    MarketItemPriceSnapshot(itemCode = 1000, price = 3000)
                )
                it("저장된 건수가 4건") {
                    val savedItems = marketItemPriceSnapshotRepo.findAll()
                    savedItems.isNotEmpty() shouldBe true
                    savedItems.size shouldBe 4
                }
            }
        }
        xdescribe("다건 저장 테스트") {
            context("중복 없는 다건 저장시") {
                val items = mutableListOf<MarketItemPriceSnapshot>()
                for (i in 1..2) {
                    for (j in 1..10) {
                        items.add(
                            MarketItemPriceSnapshot(
                                itemCode = i * 1000,
                                price = j * 1000
                            )
                        )
                    }
                }
                marketItemPriceSnapshotRepo.saveAllIgnoreDuplicates(items)
                it("저장된 건수가 20건") {
                    val savedItems = marketItemPriceSnapshotRepo.findAll()
                    savedItems.isNotEmpty() shouldBe true
                    savedItems.size shouldBe 20
                }
            }
            context("중복 없는다건 저장시") {
                val items = mutableListOf<MarketItemPriceSnapshot>()
                for (i in 1..2) {
                    for (j in 1..10) {
                        items.add(
                            MarketItemPriceSnapshot(
                                itemCode = i * 1000,
                                price = j * 1000
                            )
                        )
                    }
                }
                marketItemPriceSnapshotRepo.saveAllIgnoreDuplicates(items)
                val savedItems = marketItemPriceSnapshotRepo.findAll()

                val duplicateItems = mutableListOf<MarketItemPriceSnapshot>()
                for (i in 1..2) {
                    for (j in 1..5) {
                        duplicateItems.add(
                            MarketItemPriceSnapshot(
                                itemCode = i * 1000,
                                price = j * 1000
                            )
                        )
                    }
                }
                marketItemPriceSnapshotRepo.saveAllIgnoreDuplicates(duplicateItems)
                val savedDuplicateItems = marketItemPriceSnapshotRepo.findAll()
                it("기존 저장된 건수가 20건") {
                    savedItems.isNotEmpty() shouldBe true
                    savedItems.size shouldBe 20
                }
                it("중복된 값을 저장 후에도 저장된 건수가 20건") {
                    savedDuplicateItems.isNotEmpty() shouldBe true
                    savedDuplicateItems.size shouldBe 20
                }
            }
        }

        describe("이상치 제거 테스트") {
            context("비슷한 데이터 분포인 경우") {
                val testPrices: MutableList<Int> = mutableListOf()
                for (i in 1..100) {
                    testPrices.add(900 + i)
                }
                val snapshots =
                    testPrices.map { price ->
                        MarketItemPriceSnapshot(
                            itemCode = 1000,
                            price = price
                        )
                    }
                marketItemPriceSnapshotRepo.saveAllIgnoreDuplicates(snapshots)
                it("이상치를 제거 없이 결과를 반환") {
                    val savedItems = marketItemPriceSnapshotRepo.findFilteredPriceRangeByItemCode(1000)
                    savedItems.shouldNotBeNull()
                    savedItems.min shouldBe 901
                    savedItems.max shouldBe 1000
                }
            }
            context("음수 가격이 포함된 경우") {
                val testPrices = mutableListOf(-1, -3, -10, -30 -50, -100, -200)
                for (i in 1..100) {
                    testPrices.add(100 + i)
                }
                val snapshots =
                    testPrices.map { price ->
                        MarketItemPriceSnapshot(
                            itemCode = 2000,
                            price = price
                        )
                    }
                marketItemPriceSnapshotRepo.saveAllIgnoreDuplicates(snapshots)
                it("음수를 제외한 결과를 반환") {
                    val savedItems = marketItemPriceSnapshotRepo.findFilteredPriceRangeByItemCode(2000)
                    savedItems.shouldNotBeNull()
                    savedItems.min shouldBe 101
                    savedItems.max shouldBe 200
                }
            }
            context("극닥적인 최저값과 최고값이 포함된 경우") {
                val testPrices = mutableListOf(1, 2, 3, 10, 15, 50, 2000, 2500, 9000, 10000, 20000)
                for (i in 1..100) {
                    testPrices.add(900 + i)
                }

                val snapshots =
                    testPrices.map { price ->
                        MarketItemPriceSnapshot(
                            itemCode = 3000,
                            price = price
                        )
                    }
                marketItemPriceSnapshotRepo.saveAllIgnoreDuplicates(snapshots)
                it("이상치를 제외한 결과를 반환") {
                    val savedItems = marketItemPriceSnapshotRepo.findFilteredPriceRangeByItemCode(3000)
                    savedItems.shouldNotBeNull()
                    savedItems.min shouldBe 901
                    savedItems.max shouldBe 1000
                }
            }
        }
    }
}