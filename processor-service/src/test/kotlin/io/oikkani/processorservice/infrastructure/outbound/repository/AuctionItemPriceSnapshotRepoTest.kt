package io.oikkani.processorservice.infrastructure.outbound.repository

import com.github.f4b6a3.tsid.TsidCreator
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.oikkani.processorservice.application.port.outbound.AuctionItemPriceSnapshotRepositoryPort
import io.oikkani.processorservice.application.dto.AuctionItemPriceSnapshotDTO
import io.oikkani.processorservice.infrastructure.config.repository.PostgresqlTestContainersConfig
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.AuctionItemPriceSnapshotJpaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
@Import(PostgresqlTestContainersConfig::class)
class AuctionItemPriceSnapshotRepoTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var repository: AuctionItemPriceSnapshotRepositoryPort

    @Autowired
    private lateinit var jpaRepository: AuctionItemPriceSnapshotJpaRepository

    val timeNow: LocalDateTime = LocalDateTime.now()

    init {
        describe("saveAllIgnoreDuplicates method test") {
            beforeContainer {
                repository.deleteAll()
            }

            context("빈 리스트를 전달했을 때") {
                repository.saveAllNotExists(emptyList())
                it("아무것도 저장되지 않아야 함") {
                    val savedItems = jpaRepository.findAll()
                    savedItems shouldHaveSize 0
                }
            }
            context("새로운 데이터 리스트를 전달했을 때") {
                val testSnapshots = listOf(
                    AuctionItemPriceSnapshotDTO(
                        id = TsidCreator.getTsid().toLong(),
                        itemCode = 12345,
                        endDate = LocalDateTime.now(),
                        price = 10000
                    ),
                    AuctionItemPriceSnapshotDTO(
                        id = TsidCreator.getTsid().toLong(),
                        itemCode = 12346,
                        endDate = LocalDateTime.now(),
                        price = 15000
                    ),
                    AuctionItemPriceSnapshotDTO(
                        id = TsidCreator.getTsid().toLong(),
                        itemCode = 12347,
                        endDate = LocalDateTime.now(),
                        price = 20000
                    )
                )

                it("모든 데이터가 정상적으로 저장되어야 함") {
                    repository.saveAllNotExists(testSnapshots)

                    val savedItems = jpaRepository.findAll()
                    savedItems shouldHaveSize 3
                    savedItems.map { it.itemCode } shouldBe listOf(12345, 12346, 12347)
                    savedItems.map { it.price } shouldBe listOf(10000, 15000, 20000)
                }
            }
            context("중복된 ID가 포함된 데이터를 전달했을 때") {
                val duplicateId = TsidCreator.getTsid().toLong()
                val testSnapshots = listOf(
                    AuctionItemPriceSnapshotDTO(
                        id = duplicateId,
                        itemCode = 12345,
                        endDate = LocalDateTime.now(),
                        price = 10000
                    ),
                    AuctionItemPriceSnapshotDTO(
                        id = duplicateId, // 중복된 ID
                        itemCode = 12346,
                        endDate = LocalDateTime.now(),
                        price = 15000
                    )
                )

                it("중복을 무시하고 첫 번째 데이터만 저장되어야 함") {
                    repository.saveAllNotExists(testSnapshots)

                    val savedItems = jpaRepository.findAll()
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
                repository.saveAllNotExists(testSnapshots)
                it("중복을 무시하고 3건만 저장") {
                    val savedItems = jpaRepository.findAll()
                    savedItems shouldHaveSize 3
                }
            }
            context("ID가 null인 데이터를 전달했을 때") {
                val testSnapshots = listOf(
                    AuctionItemPriceSnapshotDTO(
                        id = null, // ID가 null
                        itemCode = 12345,
                        endDate = LocalDateTime.now(),
                        price = 10000
                    ),
                    AuctionItemPriceSnapshotDTO(
                        id = null, // ID가 null
                        itemCode = 12346,
                        endDate = LocalDateTime.now(),
                        price = 15000
                    )
                )

                it("자동으로 ID가 생성되어 저장되어야 함") {
                    repository.saveAllNotExists(testSnapshots)

                    val savedItems = jpaRepository.findAll()
                    savedItems shouldHaveSize 2
                    savedItems.forEach { item ->
                        item.id shouldNotBe null
                        item.id!! shouldBeGreaterThan 0L
                    }
                }
            }


        }

        describe("truncateTable 메서드") {
            beforeContainer {
                repository.deleteAll()
            }

            context("데이터가 있는 상태에서 truncate를 실행했을 때") {
                val testSnapshots = listOf(
                    createTestSnapshot(12345, 10000),
                    createTestSnapshot(12346, 15000),
                    createTestSnapshot(12347, 20000)
                )
                repository.saveAllNotExists(testSnapshots)

                it("모든 데이터가 삭제되어야 함") {
                    // 데이터가 있는지 확인
                    jpaRepository.findAll() shouldHaveSize 3

                    // truncate 실행
                    repository.deleteAll()

                    // 데이터가 모두 삭제되었는지 확인
                    jpaRepository.findAll() shouldHaveSize 0
                }
            }

            context("빈 테이블에서 truncate를 실행했을 때") {
                it("예외가 발생하지 않아야 함") {
                    // 빈 테이블에서 truncate 실행
                    repository.deleteAll()

                    // 여전히 빈 상태여야 함
                    jpaRepository.findAll() shouldHaveSize 0
                }
            }
        }
    }

    private fun createTestSnapshot(itemCode: Int, price: Int): AuctionItemPriceSnapshotDTO {
        return AuctionItemPriceSnapshotDTO(
            id = TsidCreator.getTsid().toLong(),
            itemCode = itemCode,
            endDate = timeNow,
            price = price
        )
    }
}