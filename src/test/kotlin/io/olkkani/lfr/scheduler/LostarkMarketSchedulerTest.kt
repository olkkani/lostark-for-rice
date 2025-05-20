package io.olkkani.lfr.scheduler

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.olkkani.lfr.config.TestContainersConfig
import io.olkkani.lfr.repository.MarketItemPriceSnapshotRepo
import io.olkkani.lfr.service.LostarkMarketScheduler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainersConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LostarkMarketSchedulerTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)
    @Autowired
    private lateinit var scheduler: LostarkMarketScheduler

    @Autowired
    private lateinit var snapshotRepo: MarketItemPriceSnapshotRepo

    init {
        describe("Lostark Market Scheduler Test") {
            context("유물 각인서 가져올 경우") {
                scheduler.fetchEngravingRecipePriceAndUpdatePrice()
                it("유물 각인서 개수가 10 이상") {
                    snapshotRepo.findAll().size shouldBeGreaterThan 10
                }
            }
        }
    }
}