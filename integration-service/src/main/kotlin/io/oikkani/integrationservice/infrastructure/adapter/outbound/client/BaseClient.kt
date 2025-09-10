package io.oikkani.integrationservice.infrastructure.adapter.outbound.client

import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import io.oikkani.integrationservice.domain.dto.AlertError
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
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
abstract class BaseClient(
    private val exceptionNotification: ExceptionNotification,
) {

    /**
     * 공통 재시도 정책 Extension Function
     *
     * 사용법:
     * webClient.get().uri("/api").retrieve()
     *     .bodyToMono(ResponseClass::class.java)
     *     .withCommonRetry(exceptionNotification, "API_NAME")
     */
    fun <T> Mono<T>.withCommonRetry(
        actionName: String = "Unknown API"
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

                    val errorStatus = when (originalError) {
                        is WebClientResponseException -> "HTTP ${originalError.statusCode.value()}"
                        else -> originalError.javaClass.simpleName
                    }
                    // Discord 알람 전송
                    exceptionNotification.sendErrorNotification(
                        AlertError(
                            actionName = actionName,
                            retryAttempts = retrySignal.totalRetries().toInt() + 1,
                            errorCode = originalError.hashCode(),
                            errorStatus = errorStatus,
                            errorMessage = originalError.message ?: "Unknown error",
                        )
                    )
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
            is ConnectException,
            is SocketTimeoutException,
            is IOException -> true

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
     * 공통 재시도 정책을 적용하고 바로 구독하는 Extension Function (Fire-and-Forget 패턴)
     *
     * 사용법:
     * webClient.post().uri("/api").bodyValue(data).retrieve()
     *     .bodyToMono(Unit::class.java)
     *     .withCommonRetryAndSubscribe(exceptionNotification, "API_NAME")
     *
     * 결과를 기다리지 않고 바로 처리하며, 에러 시 Discord 알림만 전송
     */
    suspend fun <T> Mono<T>.withCommonRetryAndSubscribe(
        actionName: String = "Unknown API"
    ) {
        this.withCommonRetry(actionName)
            .awaitSingle()
    }
}