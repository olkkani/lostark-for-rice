package io.oikkani.integrationservice.infrastructure.out.client.lostark

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.floats.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.oikkani.integrationservice.config.security.TestSecurityConfig
import io.oikkani.integrationservice.domain.dto.MarketItemCondition
import io.oikkani.integrationservice.infrastructure.outbound.client.lostark.MarketClient
import io.olkkani.common.dto.contract.MarketItemPrice
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig::class)
class MarketClientTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var client: MarketClient

    init {
        xdescribe("MarketClient Test") {
            context("단 건 조회를 하면") {
                val abidos = MarketItemCondition(
                    categoryCode = 50010,
                    itemCode = 6861012,
                    itemName = "아비도스 융화 재료"
                )
                val response = client.fetchItems(abidos.toFusionMaterialRequest())
                it("누락 데이터 없이 매핑") {
                    response shouldNotBe null
                    response?.let {
                        it.items.size shouldBe 1
                        it.items[0].id shouldBe abidos.itemCode
                        it.items[0].currentMinPrice shouldBeGreaterThan 0
                        it.items[0].yDayAvgPrice shouldBeGreaterThan 0F
                    }
                }
            }
            context("다 건 조회를 하면") {
                val prices = mutableListOf<MarketItemPrice>()
                val itemCondition = MarketItemCondition(
                    categoryCode = 40000,
                    itemGrade = "유물"
                )
                (1.. 5).map {
                    val request = itemCondition.toRelicEngravingRecipeRequest(it)
                    val response = client.fetchItems(request)

                    response?.let { responseMarketPrices ->
                        prices.addAll(responseMarketPrices.extractPrices())
                    }
                }
                it("데이터 매핑") {
                    prices.size shouldBeGreaterThan 0
                }
            }
        }
    }
}