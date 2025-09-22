package io.oikkani.processorservice.infrastructure.outbound.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.oikkani.processorservice.application.port.outbound.MarketItemPriceSnapshotRepositoryPort
import io.oikkani.processorservice.infrastructure.config.repository.PostgresqlTestContainersConfig
import io.oikkani.processorservice.infrastructure.config.security.TestSecurityConfig
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.MarketItemPriceSnapshot
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.MarketItemPriceSnapshotJpaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig::class, PostgresqlTestContainersConfig::class)
class MarketItemPriceSnapshotRepoTest : DescribeSpec() {

    @Autowired
    private lateinit var repository: MarketItemPriceSnapshotRepositoryPort

    @Autowired
    private lateinit var jpaRepository: MarketItemPriceSnapshotJpaRepository

    init {
        describe("delete test") {
            context("10개의 레코드가 있는 상태에서 삭제하면") {
                val testData = listOf<MarketItemPriceSnapshot>(
                    MarketItemPriceSnapshot(itemCode = 1000, price = 1000),
                    MarketItemPriceSnapshot(itemCode = 1000, price = 1001),
                    MarketItemPriceSnapshot(itemCode = 1001, price = 1000),
                    MarketItemPriceSnapshot(itemCode = 1001, price = 1001),
                    MarketItemPriceSnapshot(itemCode = 1002, price = 1000),
                    MarketItemPriceSnapshot(itemCode = 1002, price = 1001),
                    MarketItemPriceSnapshot(itemCode = 1003, price = 1000),
                    MarketItemPriceSnapshot(itemCode = 1003, price = 1001),
                    MarketItemPriceSnapshot(itemCode = 1004, price = 1000),
                    MarketItemPriceSnapshot(itemCode = 1004, price = 1001),
                )
                repository.saveAllNotExists(testData)
                it("삭제 전 데이터는 10개") {
                    jpaRepository.findAll().size shouldBe 10
                }

                repository.deleteAll()
                it("레코드가 0개 존재") {
                    jpaRepository.findAll().size shouldBe 0
                }
            }
        }
        describe("save test") {
            context("if total 10 duplicate records 5 save") {
                val testData = listOf<MarketItemPriceSnapshot>(
                    MarketItemPriceSnapshot(itemCode = 1000, price = 1000),
                    MarketItemPriceSnapshot(itemCode = 1000, price = 1000),
                    MarketItemPriceSnapshot(itemCode = 1001, price = 1000),
                    MarketItemPriceSnapshot(itemCode = 1001, price = 1000),
                    MarketItemPriceSnapshot(itemCode = 1002, price = 1000),
                    MarketItemPriceSnapshot(itemCode = 1002, price = 1000),
                    MarketItemPriceSnapshot(itemCode = 1003, price = 1000),
                    MarketItemPriceSnapshot(itemCode = 1003, price = 1000),
                    MarketItemPriceSnapshot(itemCode = 1004, price = 1000),
                    MarketItemPriceSnapshot(itemCode = 1004, price = 1000),
                )
                repository.saveAllNotExists(testData)
                it("total 5 records saved") {
                    jpaRepository.findAll().size shouldBe 5
                }
            }
        }

    }

}