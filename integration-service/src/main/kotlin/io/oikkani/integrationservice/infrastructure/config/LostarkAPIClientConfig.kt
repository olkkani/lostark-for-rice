package io.oikkani.integrationservice.infrastructure.config

import io.oikkani.integrationservice.application.port.out.ExceptionNotification
import io.oikkani.integrationservice.infrastructure.adapter.out.client.LostarkAPIClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LostarkAPIClientConfig {

    @Bean("auctionAPIClient")
    fun auctionAPIClient(
        @param:Value("\${lostark.auction.api.key:must-not-null-auction-apikey}") apiKey: String,
        exceptionNotification: ExceptionNotification
    ): LostarkAPIClient {
        return LostarkAPIClient(apiKey, exceptionNotification)
    }

    @Bean("marketAPIClient")
    fun marketAPIClient(
        @param:Value("\${lostark.market.api.key:must-not-null-market-apikey}") apiKey: String,
        exceptionNotification: ExceptionNotification
    ): LostarkAPIClient {
        return LostarkAPIClient(apiKey, exceptionNotification)
    }
}