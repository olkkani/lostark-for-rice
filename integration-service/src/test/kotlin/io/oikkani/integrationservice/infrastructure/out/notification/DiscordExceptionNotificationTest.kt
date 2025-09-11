package io.oikkani.integrationservice.infrastructure.out.notification

import io.kotest.common.runBlocking
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.oikkani.integrationservice.config.security.TestSecurityConfig
import io.oikkani.integrationservice.domain.dto.AlertError
import io.oikkani.integrationservice.infrastructure.adapter.outbound.notofication.DiscordExceptionNotificationImpl
import kotlinx.coroutines.delay
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig::class)
class DiscordExceptionNotificationTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var notification: DiscordExceptionNotificationImpl

    init {
        xdescribe("Discord Exception Notification Test") {
            context("Exception Notification Test") {
                val alertError = AlertError(
                    actionName = "Error Title",
                    errorCode = 400,
                    errorStatus = "Bad Request",
                    errorMessage = "Bad Request Test Message"
                )
                it("Exception Notification Test") {
                    runBlocking {
                        notification.sendErrorNotification(alertError)
                        delay(2000)
                    }
                }
            }
        }
    }


}