package io.olkkani.lfr.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.olkkani.lfr.dao.gems
import io.olkkani.lfr.dao.toRequest
import io.olkkani.lfr.util.LostarkAPIClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class DiscordWebHookTest: DescribeSpec(){
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var apiClient: LostarkAPIClient

    private val log = KotlinLogging.logger {}

    init {
        this.describe("Discord Webhook Test"){
            context("환경변수로 잘못된 api url 을 입력하면"){
                it("Webhook 알림"){
                    try {
                        val response = apiClient.fetchAuctionItemsSubscribe(gems.first().toRequest())
                    } catch (error: Exception) {
                        log.error{error}
                    }
                }
            }
        }
    }
}