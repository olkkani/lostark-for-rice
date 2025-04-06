package io.olkkani.lfr.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.olkkani.lfr.util.LostarkAPIClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Ignore

@ActiveProfiles("test")
@SpringBootTest
@Ignore
class DiscordWebHookTest(
    @Autowired
    private var apiClient: LostarkAPIClient
): DescribeSpec(){
    override fun extensions() = listOf(SpringExtension)



    private val log = KotlinLogging.logger {}

    init {
        this.describe("Discord Webhook Test"){
            context("환경변수로 잘못된 api url 을 입력하면"){
                it("Webhook 알림"){
                }
            }
        }
    }
}