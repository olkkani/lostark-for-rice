package io.oikkani.integrationservice.infrastructure.out.client.lostark

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.oikkani.integrationservice.config.security.TestSecurityConfig
import io.oikkani.integrationservice.domain.dto.AuctionItemCondition
import io.oikkani.integrationservice.infrastructure.outbound.client.lostark.AuctionClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig::class)
class AuctionClientTest: DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)
    @Autowired
    private lateinit var client: AuctionClient

    init {

        val gemRequest = AuctionItemCondition(itemCode = 65031080, pairItemCode = 65032080, itemName = "8레벨 겁화의 보석").toGemRequest()

        xdescribe("AuctionClient Test"){
            context("실제 Request 로 데이터를 요청하면 "){
                val response = client.fetchItems(gemRequest)
                it("데이터를 가져옴"){
                    print(response.toString())
                }
            }
        }

    }
}