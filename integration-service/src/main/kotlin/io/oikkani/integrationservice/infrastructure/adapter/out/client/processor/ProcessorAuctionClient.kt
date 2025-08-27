package io.oikkani.integrationservice.infrastructure.adapter.out.client.processor

import io.oikkani.integrationservice.application.port.out.ProcessorBaseClient
import io.oikkani.integrationservice.external.dto.AuctionItem
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.util.retry.Retry

class ProcessorAuctionClient : ProcessorBaseClient() {
    
    private val logger = LoggerFactory.getLogger(ProcessorAuctionClient::class.java)
    
    fun processAuctionItems(auctionItems: List<AuctionItem>): Mono<String> {
        return client.post()
            .uri("")
            .bodyValue(auctionItems)
            .retrieve()
            .bodyToMono(String::class.java)
            .timeout(requestTimeout) // timeout 설정 추가
            .retryWhen(
                Retry.backoff(maxRetryAttempts, initialDelay)
                    .maxBackoff(maxBackoff)
                    .filter { throwable ->
                        when {
                            throwable is WebClientResponseException -> {
                                val statusCode = throwable.statusCode
                                val shouldRetry = shouldRetryForStatusCode(statusCode)
                                
                                logger.warn(
                                    "HTTP error occurred - Status: {}, Message: {}, Will retry: {}",
                                    statusCode.value(),
                                    throwable.message,
                                    shouldRetry
                                )
                                
                                shouldRetry
                            }
                            else -> {
                                logger.warn("Non-HTTP error occurred: {}", throwable.message)
                                false // HTTP 에러가 아닌 경우는 재시도하지 않음
                            }
                        }
                    }
                    .doBeforeRetry { retrySignal ->
                        logger.info(
                            "Retrying request - Attempt: {}/{}, Delay: {}ms, Cause: {}",
                            retrySignal.totalRetries() + 1,
                            maxRetryAttempts,
                            retrySignal.totalRetriesInARow(),
                            retrySignal.failure().message
                        )
                    }
                    .onRetryExhaustedThrow { _, retrySignal ->
                        logger.error(
                            "All retry attempts exhausted - Total attempts: {}, Final error: {}",
                            retrySignal.totalRetries(),
                            retrySignal.failure().message
                        )
                        retrySignal.failure()
                    }
            )
            .doOnSuccess { response ->
                logger.info("Auction items processed successfully")
            }
            .doOnError { error ->
                // ✅ 사이드 이펙트: 로깅, 메트릭 수집
                logger.error("Failed to process auction items after all retries: {}", error.message)
                recordFailureMetrics(error)
            }
            // ✅ onErrorMap: CustomErrorException으로 변환하여 알림 가능
            .onErrorMap { originalError ->
                createCustomException(originalError, maxRetryAttempts.toInt())
            }
    }

    /**
     * HTTP 상태 코드에 따라 재시도 여부를 결정
     */
    private fun shouldRetryForStatusCode(statusCode: HttpStatus): Boolean {
        return when {
            statusCode.is5xxServerError -> true
            statusCode == HttpStatus.TOO_MANY_REQUESTS -> true
            statusCode == HttpStatus.REQUEST_TIMEOUT -> true
            else -> {
                logger.debug("4xx client error - not retrying: {}", statusCode.value())
                false
            }
        }
    }

    /**
     * 원본 에러를 ProcessorClientException으로 변환
     */
    private fun createCustomException(originalError: Throwable, retryAttempts: Int): ProcessorClientException {
        return when (originalError) {
            is WebClientResponseException -> {
                when {
                    originalError.statusCode.is4xxClientError -> {
                        ProcessorClientException(
                            message = "클라이언트 요청 오류: ${originalError.message}",
                            errorCode = ProcessorErrorCode.CLIENT_ERROR_4XX,
                            httpStatus = originalError.statusCode,
                            retryAttempts = retryAttempts,
                            cause = originalError
                        )
                    }
                    originalError.statusCode.is5xxServerError -> {
                        ProcessorClientException(
                            message = "서버 내부 오류: ${originalError.message}",
                            errorCode = ProcessorErrorCode.SERVER_ERROR_5XX,
                            httpStatus = originalError.statusCode,
                            retryAttempts = retryAttempts,
                            cause = originalError
                        )
                    }
                    else -> {
                        ProcessorClientException(
                            message = "HTTP 오류: ${originalError.message}",
                            errorCode = ProcessorErrorCode.UNKNOWN_ERROR,
                            httpStatus = originalError.statusCode,
                            retryAttempts = retryAttempts,
                            cause = originalError
                        )
                    }
                }
            }
            else -> {
                // 네트워크, 타임아웃 에러 등
                val errorCode = when {
                    originalError.message?.contains("timeout", ignoreCase = true) == true -> 
                        ProcessorErrorCode.TIMEOUT_ERROR
                    else -> ProcessorErrorCode.NETWORK_ERROR
                }
                
                ProcessorClientException(
                    message = "네트워크 또는 타임아웃 오류: ${originalError.message}",
                    errorCode = errorCode,
                    retryAttempts = retryAttempts,
                    cause = originalError
                )
            }
        }
    }

    /**
     * 실패 메트릭 수집
     */
    private fun recordFailureMetrics(error: Throwable) {
        logger.debug("Recording failure metrics for: {}", error.javaClass.simpleName)
        // 실제로는 Micrometer 등을 사용해서 메트릭 수집
        // meterRegistry.counter("processor.client.failures").increment()
    }
}

/**
 * 프로세서 클라이언트 에러를 위한 커스텀 예외
 */
class ProcessorClientException(
    message: String,
    val errorCode: ProcessorErrorCode,
    val httpStatus: HttpStatus? = null,
    val retryAttempts: Int = 0,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    
    val timestamp: Long = System.currentTimeMillis()
    
    override fun toString(): String {
        return "ProcessorClientException(errorCode=$errorCode, httpStatus=$httpStatus, retryAttempts=$retryAttempts, message='$message')"
    }
}

/**
 * 프로세서 에러 코드 분류
 */
enum class ProcessorErrorCode(
    val code: String,
    val description: String,
    val shouldAlert: Boolean = true
) {
    NETWORK_ERROR("PC001", "네트워크 연결 실패", true),
    TIMEOUT_ERROR("PC002", "요청 타임아웃", true),
    CLIENT_ERROR_4XX("PC003", "클라이언트 요청 오류 (4xx)", false),
    SERVER_ERROR_5XX("PC004", "서버 내부 오류 (5xx)", true),
    RETRY_EXHAUSTED("PC005", "모든 재시도 실패", true),
    UNKNOWN_ERROR("PC999", "알 수 없는 오류", true)
}