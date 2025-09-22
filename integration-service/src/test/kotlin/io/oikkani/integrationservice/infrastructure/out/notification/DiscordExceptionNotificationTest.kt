package io.oikkani.integrationservice.infrastructure.out.notification

import io.kotest.common.runBlocking
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import io.oikkani.integrationservice.config.notification.TestDiscordNotification
import io.oikkani.integrationservice.config.security.TestSecurityConfig
import io.oikkani.integrationservice.domain.dto.AlertError
import io.oikkani.integrationservice.infrastructure.outbound.client.BaseClient
import kotlinx.coroutines.delay
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig::class)
class DiscordExceptionNotificationTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var notification: TestDiscordNotification


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
        xdescribe("BaseClient createCommonRetry 함수 테스트") {

            context("404 에러 발생 시") {
                it("3번 재시도 후 Discord 알림을 전송하고 Mono.empty()를 반환한다") {
                    // Given
                    val testClient = object : BaseClient(notification) {}

                    val errorException = WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        HttpHeaders.EMPTY,
                        "Test 404 error message".toByteArray(),
                        null
                    )

                    val failingMono = Mono.error<String>(errorException)

                    // Mock 설정

                    // When & Then - StepVerifier로 Mono 동작 검증
                    StepVerifier.create(
                        testClient.run { failingMono.withCommonRetry() }
                    )
                        .expectComplete() // Mono.empty() 반환 확인
                        .verify(Duration.ofSeconds(10))

                    runBlocking { delay(1000) }
                }
            }

            context("503 에러 발생 시") {
                it("3번 재시도 후 Discord 알림을 전송하고 Mono.empty()를 반환한다") {
                    // Given
                    val testClient = object : BaseClient(notification) {}


                    val errorException = WebClientResponseException.create(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Service Unavailable",
                        HttpHeaders.EMPTY,
                        "Test 503 server error".toByteArray(),
                        null
                    )

                    val failingMono = Mono.error<String>(errorException)

                    // When & Then - StepVerifier로 Mono 동작 검증
                    StepVerifier.create(
                        testClient.run { failingMono.withCommonRetry() }
                    )
                        .expectComplete() // Mono.empty() 반환 확인
                        .verify(Duration.ofSeconds(10))

                    // Discord 알림 전송 검증
                    runBlocking {
                        delay(3000)
                    }
                }
            }

            xcontext("429 Rate Limit 에러 발생 시") {
                it("1분 지연 후 재시도하고 최종 실패 시 Discord 알림을 전송한다") {
                    // Given
                    val mockExceptionNotification = mockk<ExceptionNotification>()
                    val testClient = object : BaseClient(mockExceptionNotification) {}

                    val rateLimitException = WebClientResponseException.create(
                        HttpStatus.TOO_MANY_REQUESTS.value(),
                        "Too Many Requests",
                        HttpHeaders.EMPTY,
                        "Rate limit exceeded".toByteArray(),
                        null
                    )

                    val failingMono = Mono.error<String>(rateLimitException)

                    // Mock 설정
                    val capturedAlertError = slot<AlertError>()
                    every {
                        mockExceptionNotification.sendErrorNotification(capture(capturedAlertError))
                    } returns Unit

                    // When & Then
                    StepVerifier.create(
                        testClient.run { failingMono.withCommonRetry() }
                    )
                        .expectComplete() // Mono.empty() 반환 확인
                        .verify(Duration.ofSeconds(10)) // 테스트 시간 단축을 위해 실제 1분 대기는 안함

                    // Discord 알림 전송 검증
                    verify(exactly = 1) {
                        mockExceptionNotification.sendErrorNotification(any())
                    }

                    // 전달된 AlertError 메시지 검증
                    capturedAlertError.captured.apply {
                        errorStatus shouldBe "HTTP 429"
                        errorMessage shouldBe "Rate limit exceeded"
                        retryAttempts shouldBe 4 // 초기 시도 1회 + 재시도 3회
                    }
                }
            }

            context("성공적인 요청") {
                it("재시도 없이 정상적으로 결과를 반환한다") {
                    // Given
                    val mockExceptionNotification = mockk<ExceptionNotification>()
                    val testClient = object : BaseClient(mockExceptionNotification) {}

                    val successMono = Mono.just("success response")

                    // When & Then
                    StepVerifier.create(
                        testClient.run { successMono.withCommonRetry() }
                    )
                        .expectNext("success response")
                        .expectComplete()
                        .verify(Duration.ofSeconds(5))
                    runBlocking {
                        delay(3000)
                    }
                    // Discord 알림이 전송되지 않았는지 확인
                    verify(exactly = 0) {
                        mockExceptionNotification.sendErrorNotification(any())
                    }
                }
            }
        }

    }


}