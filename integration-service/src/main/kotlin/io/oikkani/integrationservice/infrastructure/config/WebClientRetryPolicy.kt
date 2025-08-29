package io.oikkani.integrationservice.infrastructure.config

import io.oikkani.integrationservice.application.port.out.ExceptionNotification
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

/**
 * WebClient 공통 재시도 정책
 *
 * 요구사항:
 * - 4xx, 5xx 에러: 1초 간격, 최대 3회 재시도, jitter 0.1
 * - 429 Rate Limit: 1분 논블로킹 대기
 * - 최종 실패 시: Discord 알람 + Mono.empty() 반환
 * - 로그 없음
 */
object WebClientRetryPolicy {

    /**
     * 공통 재시도 정책 Extension Function
     *
     * 사용법:
     * webClient.get().uri("/api").retrieve()
     *     .bodyToMono(ResponseClass::class.java)
     *     .withCommonRetry(exceptionNotification, "API_NAME")
     */
    fun <T> Mono<T>.withCommonRetry(
        exceptionNotification: ExceptionNotification,
        apiName: String = "Unknown API"
    ): Mono<T> {
        return this.retryWhen(
            Retry.backoff(3, Duration.ofSeconds(1))
                .jitter(0.1) // 10% 지터로 thundering herd 방지
                .filter { error -> isRetryableError(error) }
                // 429 Rate Limit 시 1분 비동기 논블로킹 대기
                .doBeforeRetryAsync { retrySignal ->
                    val error = retrySignal.failure()

                    if (isRateLimitError(error)) {
                        // 🚀 429 Rate Limit: 1분 논블로킹 대기
                        Mono.delay(Duration.ofMinutes(1)).then()
                    } else {
                        // 일반 4xx, 5xx: 지연 없음 (기본 백오프 사용)
                        Mono.empty()
                    }
                }
                // 재시도 실패 시 Discord 알람 전송
                .onRetryExhaustedThrow { _, retrySignal ->
                    val originalError = retrySignal.failure()
                    val alertMessage = createDiscordAlert(
                        apiName = apiName,
                        error = originalError,
                        retryAttempts = retrySignal.totalRetries().toInt() + 1
                    )
                    // Discord 알람 전송
                    exceptionNotification.sendErrorNotification(alertMessage, "api_retry_exhausted")
                    originalError
                }
        )
            // 최종 에러 발생 시 Mono.empty() 반환
            .onErrorResume { Mono.empty() }
    }

    /**
     * 재시도 가능한 에러인지 판단
     */
    private fun isRetryableError(error: Throwable): Boolean {
        return when (error) {
            is WebClientResponseException -> {
                val statusCode = error.statusCode
                // 4xx, 5xx 에러 모두 재시도
                statusCode.is4xxClientError || statusCode.is5xxServerError
            }
            // 네트워크 에러들도 재시도 대상
            is java.net.ConnectException,
            is java.net.SocketTimeoutException,
            is java.io.IOException -> true

            else -> false
        }
    }

    /**
     * Rate Limit 에러 (429) 판단
     */
    private fun isRateLimitError(error: Throwable): Boolean {
        return error is WebClientResponseException &&
                error.statusCode == HttpStatus.TOO_MANY_REQUESTS
    }

    /**
     * Discord 알람 메시지 생성
     */
    private fun createDiscordAlert(
        apiName: String,
        error: Throwable,
        retryAttempts: Int
    ): String {
        val errorType = when (error) {
            is WebClientResponseException -> "HTTP ${error.statusCode.value()}"
            else -> error.javaClass.simpleName
        }

        return """
            🚨 API 재시도 최종 실패!
            
            **API:** $apiName
            **에러 타입:** $errorType  
            **재시도 횟수:** ${retryAttempts}번 모두 실패
            **에러 메시지:** ${error.message}
            **시간:** ${java.time.LocalDateTime.now()}
            
            ⚠️ 서비스는 빈 응답으로 계속 진행됩니다.
        """.trimIndent()
    }
}