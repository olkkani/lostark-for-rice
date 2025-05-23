package io.olkkani.lfr.config

import io.olkkani.lfr.util.ExceptionNotification
import io.olkkani.lfr.util.LostarkAPIClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LostarkAPIClientConfig {

    @Bean("auctionAPIClient")
    fun auctionAPIClient(
        @Value("\${lostark.auction.api.key}") apiKey: String,
        exceptionNotification: ExceptionNotification
    ): LostarkAPIClient {
        return LostarkAPIClient(apiKey, exceptionNotification)
    }

    @Bean("marketAPIClient")
    fun marketAPIClient(
        @Value("\${lostark.market.api.key}") apiKey: String,
        exceptionNotification: ExceptionNotification
    ): LostarkAPIClient {
        return LostarkAPIClient(apiKey, exceptionNotification)
    }
}
