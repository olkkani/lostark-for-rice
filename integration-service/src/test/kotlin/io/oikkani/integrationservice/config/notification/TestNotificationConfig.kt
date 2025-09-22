package io.oikkani.integrationservice.config.notification

import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@TestConfiguration
@Profile("test")
class TestNotificationConfig {

    @Bean
    @Primary
    fun testExceptionNotification(
        @Value("\${webhook.url.discord:must_not_empty_url}") webhookUrl: String
    ): ExceptionNotification {
        return TestDiscordNotification(webhookUrl)
    }
}
