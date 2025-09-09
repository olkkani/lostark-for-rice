package io.oikkani.integrationservice.infrastructure.out.notification

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.oikkani.integrationservice.config.security.TestSecurityConfig
import io.oikkani.integrationservice.domain.dto.AlertErrorDTO
import io.oikkani.integrationservice.infrastructure.adapter.outbound.notofication.DiscordExceptionNotificationImpl
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

        describe("Discord Exception Notification Test") {
            context("Exception Notification Test") {
                val alertError = AlertErrorDTO(
                    actionName = "Error Title",
                    errorCode = 400,
                    errorStatus = "Bad Request",
                    errorMessage = "Bad Request Test Message"
                )
                it("Exception Notification Test") {
                    notification.sendErrorNotification(alertError)

                }
            }
        }
    }


}