package io.oikkani.processorservice.infrastructure.out.repository

import com.github.f4b6a3.tsid.TsidCreator
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.olkkani.lfr.config.PostgresqlTestContainersConfig
import io.olkkani.lfr.config.TestSecurityConfig
import io.olkkani.lfr.repository.entity.AuctionItemPriceSnapshot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import kotlin.text.toLong

@SpringBootTest
@ActiveProfiles("test")
@Import(PostgresqlTestContainersConfig::class, TestSecurityConfig::class)
class AuctionItemPriceSnapshotRepoTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var auctionItemPriceSnapshotRepo: AuctionItemPriceSnapshotRepo

    val timeNow: LocalDateTime = LocalDateTime.now()

    init {
        describe("saveAllIgnoreDuplicates method test") {
            beforeContainer {
                auctionItemPriceSnapshotRepo.truncateTable()
            }

            context("빈 리스트를 전달했을 때") {
                auctionItemPriceSnapshotRepo.saveAllIgnoreDuplicates(emptyList())
                it("아무것도 저장되지 않아야 함") {
                    val savedItems = auctionItemPriceSnapshotRepo.findAll()
                    savedItems shouldHaveSize 0
                }
            }
            context("새로운 데이터 리스트를 전달했을 때") {
                val testSnapshots = listOf(
                    AuctionItemPriceSnapshot(
                        id = TsidCreator.getTsid().toLong(),
                        itemCode = 12345,
                        endDate = LocalDateTime.now(),
                        price = 10000
                    ),
                    AuctionItemPriceSnapshot(
                        id = TsidCreator.getTsid().toLong(),
                        itemCode = 12346,
                        endDate = LocalDateTime.now(),
                        price = 15000
                    ),
                    AuctionItemPriceSnapshot(
                        id = TsidCreator.getTsid().toLong(),
                        itemCode = 12347,
                        endDate = LocalDateTime.now(),
                        price = 20000
                    )
                )

                it("모든 데이터가 정상적으로 저장되어야 함") {
                    auctionItemPriceSnapshotRepo.saveAllIgnoreDuplicates(testSnapshots)

                    val savedItems = auctionItemPriceSnapshotRepo.findAll()
                    savedItems shouldHaveSize 3
                    savedItems.map { it.itemCode } shouldBe listOf(12345, 12346, 12347)
                    savedItems.map { it.price } shouldBe listOf(10000, 15000, 20000)
                }
            }
            context("중복된 ID가 포함된 데이터를 전달했을 때") {
                val duplicateId = TsidCreator.getTsid().toLong()
                val testSnapshots = listOf(
                    AuctionItemPriceSnapshot(
                        id = duplicateId,
                        itemCode = 12345,
                        endDate = LocalDateTime.now(),
                        price = 10000
                    ),
                    AuctionItemPriceSnapshot(
                        id = duplicateId, // 중복된 ID
                        itemCode = 12346,
                        endDate = LocalDateTime.now(),
                        price = 15000
                    )
                )

                it("중복을 무시하고 첫 번째 데이터만 저장되어야 함") {
                    auctionItemPriceSnapshotRepo.saveAllIgnoreDuplicates(testSnapshots)

                    val savedItems = auctionItemPriceSnapshotRepo.findAll()
                    savedItems shouldHaveSize 1
                    savedItems.first().itemCode shouldBe 12345
                    savedItems.first().price shouldBe 10000
                }
            }
            context("중복된 price list 를 저장했을 때") {
                val testItemCode1 = 12345
                val testItemCode2 = 123456
                val testSnapshots = listOf(
                    createTestSnapshot(testItemCode1, 10000),
                    createTestSnapshot(testItemCode1, 10000),
                    createTestSnapshot(testItemCode1, 20000),
                    createTestSnapshot(testItemCode2, 10000),
                )
                auctionItemPriceSnapshotRepo.saveAllIgnoreDuplicates(testSnapshots)
                it("중복을 무시하고 3건만 저장") {
                    val savedItems = auctionItemPriceSnapshotRepo.findAll()
                    savedItems shouldHaveSize 3
                }
            }
            context("ID가 null인 데이터를 전달했을 때") {
                val testSnapshots = listOf(
                    AuctionItemPriceSnapshot(
                        id = null, // ID가 null
                        itemCode = 12345,
                        endDate = LocalDateTime.now(),
                        price = 10000
                    ),
                    AuctionItemPriceSnapshot(
                        id = null, // ID가 null
                        itemCode = 12346,
                        endDate = LocalDateTime.now(),
                        price = 15000
                    )
                )

                it("자동으로 ID가 생성되어 저장되어야 함") {
                    auctionItemPriceSnapshotRepo.saveAllIgnoreDuplicates(testSnapshots)

                    val savedItems = auctionItemPriceSnapshotRepo.findAll()
                    savedItems shouldHaveSize 2
                    savedItems.forEach { item ->
                        item.id shouldNotBe null
                        item.id!! shouldBeGreaterThan 0L
                    }
                }
            }


        }
        describe("findFilteredPriceRangeByItemCode 메서드") {
            beforeContainer {
                auctionItemPriceSnapshotRepo.truncateTable()
            }


            val testItemCode = 99999

            context("데이터가 없을 때") {
                it("PriceRange(0, 0)을 반환해야 함") {
                    val priceRange = auctionItemPriceSnapshotRepo.findFilteredPriceRangeByItemCode(testItemCode)

                    priceRange.min shouldBe 0
                    priceRange.max shouldBe 0
                }
            }

            context("정상적인 가격 데이터가 있을 때") {
                val testSnapshots = listOf(
                    // 정상 범위의 데이터들
                    createTestSnapshot(testItemCode, 10000),
                    createTestSnapshot(testItemCode, 11000),
                    createTestSnapshot(testItemCode, 12000),
                    createTestSnapshot(testItemCode, 13000),
                    createTestSnapshot(testItemCode, 14000),
                    createTestSnapshot(testItemCode, 15000)
                )

                beforeEach {
                    auctionItemPriceSnapshotRepo.saveAllIgnoreDuplicates(testSnapshots)
                }

                it("올바른 최소값과 최대값을 반환해야 함") {
                    val priceRange = auctionItemPriceSnapshotRepo.findFilteredPriceRangeByItemCode(testItemCode)

                    priceRange.min shouldBe 10000
                    priceRange.max shouldBe 15000
                }
            }

            context("이상치가 포함된 가격 데이터가 있을 때") {
                val testSnapshots = listOf(
                    // 정상 범위의 데이터들
                    createTestSnapshot(testItemCode, 10000),
                    createTestSnapshot(testItemCode, 11000),
                    createTestSnapshot(testItemCode, 12000),
                    createTestSnapshot(testItemCode, 13000),
                    createTestSnapshot(testItemCode, 14000),
                    createTestSnapshot(testItemCode, 15000),
                    // 이상치 데이터들 (IQR 범위를 벗어남)
                    createTestSnapshot(testItemCode, 1000),   // 너무 낮은 값
                    createTestSnapshot(testItemCode, 100000), // 너무 높은 값
                    createTestSnapshot(testItemCode, 200000)  // 너무 높은 값
                )

                beforeEach {
                    auctionItemPriceSnapshotRepo.saveAllIgnoreDuplicates(testSnapshots)
                }

                it("이상치를 제외한 범위를 반환해야 함") {
                    val priceRange = auctionItemPriceSnapshotRepo.findFilteredPriceRangeByItemCode(testItemCode)

                    // IQR 계산으로 이상치가 제거되어 정상 범위만 반환되어야 함
                    priceRange.min shouldBe 10000
                    priceRange.max shouldBe 15000
                }
            }

            context("다른 itemCode의 데이터가 섞여 있을 때") {
                val otherItemCode = 88888
                val testSnapshots = listOf(
                    // 테스트 대상 itemCode 데이터
                    createTestSnapshot(testItemCode, 10000),
                    createTestSnapshot(testItemCode, 12000),
                    createTestSnapshot(testItemCode, 14000),
                    // 다른 itemCode 데이터 (필터링되어야 함)
                    createTestSnapshot(otherItemCode, 50000),
                    createTestSnapshot(otherItemCode, 60000)
                )

                auctionItemPriceSnapshotRepo.saveAllIgnoreDuplicates(testSnapshots)

                it("해당 itemCode의 데이터만 사용하여 범위를 계산해야 함") {
                    val priceRange = auctionItemPriceSnapshotRepo.findFilteredPriceRangeByItemCode(testItemCode)

                    priceRange.min shouldBe 10000
                    priceRange.max shouldBe 14000
                }
            }
        }

        describe("truncateTable 메서드") {
            beforeContainer {
                auctionItemPriceSnapshotRepo.truncateTable()
            }

            context("데이터가 있는 상태에서 truncate를 실행했을 때") {
                val testSnapshots = listOf(
                    createTestSnapshot(12345, 10000),
                    createTestSnapshot(12346, 15000),
                    createTestSnapshot(12347, 20000)
                )

                auctionItemPriceSnapshotRepo.saveAllIgnoreDuplicates(testSnapshots)

                it("모든 데이터가 삭제되어야 함") {
                    // 데이터가 있는지 확인
                    auctionItemPriceSnapshotRepo.findAll() shouldHaveSize 3

                    // truncate 실행
                    auctionItemPriceSnapshotRepo.truncateTable()

                    // 데이터가 모두 삭제되었는지 확인
                    auctionItemPriceSnapshotRepo.findAll() shouldHaveSize 0
                }
            }

            context("빈 테이블에서 truncate를 실행했을 때") {
                it("예외가 발생하지 않아야 함") {
                    // 빈 테이블에서 truncate 실행
                    auctionItemPriceSnapshotRepo.truncateTable()

                    // 여전히 빈 상태여야 함
                    auctionItemPriceSnapshotRepo.findAll() shouldHaveSize 0
                }
            }
        }
    }

    private fun createTestSnapshot(itemCode: Int, price: Int): AuctionItemPriceSnapshot {
        return AuctionItemPriceSnapshot(
            id = TsidCreator.getTsid().toLong(),
            itemCode = itemCode,
            endDate = timeNow,
            price = price
        )
    }
}