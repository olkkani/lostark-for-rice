package io.oikkani.integrationservice.infrastructure.adapter.out.client

import io.github.oshai.kotlinlogging.KotlinLogging
import io.oikkani.integrationservice.application.port.out.ExceptionNotification
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

abstract class BaseLostarkClient(
    val exceptionNotification: ExceptionNotification,
) {
    protected val logger = KotlinLogging.logger {}



    /**
     * 공통 에러 처리 로직
     */
    protected fun <T> handleError(error: Throwable, errorType: String): Mono<T> {
        exceptionNotification.sendErrorNotification(error.message.toString(), errorType)
        logger.error { "Error occurred: ${error.message}" }
        return Mono.empty()
    }

    /**
     * 공통 구독 처리 (단일 값 반환)
     */
    protected suspend fun <T> subscribeSingle(mono: Mono<T>): T {
        return mono
            .subscribeOn(Schedulers.boundedElastic())
            .awaitSingle()
    }

    /**
     * 공통 구독 처리 (nullable 값 반환)
     */
    protected suspend fun <T> subscribeSingleOrNull(mono: Mono<T>): T? {
        return mono
            .subscribeOn(Schedulers.boundedElastic())
            .awaitSingleOrNull()
    }
}