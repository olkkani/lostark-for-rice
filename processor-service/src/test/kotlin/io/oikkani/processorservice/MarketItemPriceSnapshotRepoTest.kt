package io.oikkani.processorservice

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.oikkani.processorservice.application.port.outbound.MarketItemPriceSnapshotRepositoryPort
import io.oikkani.processorservice.infrastructure.config.repository.PostgresqlTestContainersConfig
import io.oikkani.processorservice.infrastructure.config.security.TestSecurityConfig
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.MarketItemPriceSnapshot
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.MarketItemPriceSnapshotJpaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(PostgresqlTestContainersConfig::class, TestSecurityConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MarketItemPriceSnapshotRepoTest() : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var repository: MarketItemPriceSnapshotRepositoryPort

    //    @Autowired
    private lateinit var jpaRepository: MarketItemPriceSnapshotJpaRepository

    init {
        xdescribe("중복 없는 단건 저장 테스트") {
            context("중복없는 단건 4건 저장시") {
                val snapshots = mutableListOf<MarketItemPriceSnapshot>()
                for (i in 1..2) {
                    for (j in 3..4) {
                        snapshots.add(
                            MarketItemPriceSnapshot(
                                itemCode = i * 1000,
                                price = j * 1000
                            )
                        )
                    }
                }

                repository.saveAllNotExists(snapshots)
                it("저장된 건수가 4건") {
                    val savedItems = jpaRepository.findAll()
                    savedItems.isNotEmpty() shouldBe true
                    savedItems.size shouldBe 4
                }
            }
            context("노중복4건 저장 후 중복 2건을 저장하는 경우") {
                val snapshots = mutableListOf<MarketItemPriceSnapshot>()
                for (i in 1..2) {
                    for (j in 3..4) {
                        snapshots.add(
                            MarketItemPriceSnapshot(
                                itemCode = i * 1000,
                                price = j * 1000
                            )
                        )
                    }
                }
                repository.saveAllNotExists(snapshots)

                val duplicatedSnapshots = mutableListOf<MarketItemPriceSnapshot>(
                    MarketItemPriceSnapshot(itemCode = 1000, price = 3000),
                    MarketItemPriceSnapshot(itemCode = 1000, price = 3000)
                )

                repository.saveAllNotExists(duplicatedSnapshots)
                it("저장된 건수가 4건") {
                    val savedItems = jpaRepository.findAll()
                    savedItems.isNotEmpty() shouldBe true
                    savedItems.size shouldBe 4
                }
            }
        }

//        describe("이상치 제거 테스트") {
//            context("비슷한 데이터 분포인 경우") {
//                val testPrices: MutableList<Int> = mutableListOf()
//                for (i in 1..100) {
//                    testPrices.add(900 + i)
//                }
//                val snapshots =
//                    testPrices.map { price ->
//                        MarketItemPriceSnapshot(
//                            itemCode = 1000,
//                            price = price
//                        )
//                    }
//                repository.saveAllIgnoreDuplicates(snapshots)
//                it("이상치를 제거 없이 결과를 반환") {
//                    val savedItems = repository.findFilteredPriceRangeByItemCode(1000)
//                    savedItems.shouldNotBeNull()
//                    savedItems.min shouldBe 901
//                    savedItems.max shouldBe 1000
//                }
//            }
//            context("음수 가격이 포함된 경우") {
//                val testPrices = mutableListOf(-1, -3, -10, -30 - 50, -100, -200)
//                for (i in 1..100) {
//                    testPrices.add(100 + i)
//                }
//                val snapshots =
//                    testPrices.map { price ->
//                        MarketItemPriceSnapshot(
//                            itemCode = 2000,
//                            price = price
//                        )
//                    }
//                repository.saveAllIgnoreDuplicates(snapshots)
//                it("음수를 제외한 결과를 반환") {
//                    val savedItems = repository.findFilteredPriceRangeByItemCode(2000)
//                    savedItems.shouldNotBeNull()
//                    savedItems.min shouldBe 101
//                    savedItems.max shouldBe 200
//                }
//            }
//            context("극닥적인 최저값과 최고값이 포함된 경우") {
//                val testPrices = mutableListOf(1, 2, 3, 10, 15, 50, 2000, 2500, 9000, 10000, 20000)
//                for (i in 1..100) {
//                    testPrices.add(900 + i)
//                }
//
//                val snapshots =
//                    testPrices.map { price ->
//                        MarketItemPriceSnapshot(
//                            itemCode = 3000,
//                            price = price
//                        )
//                    }
//                repository.saveAllIgnoreDuplicates(snapshots)
//                it("이상치를 제외한 결과를 반환") {
//                    val savedItems = repository.findFilteredPriceRangeByItemCode(3000)
//                    savedItems.shouldNotBeNull()
//                    savedItems.min shouldBe 901
//                    savedItems.max shouldBe 1000
//                }
//            }
//        }
    }
}