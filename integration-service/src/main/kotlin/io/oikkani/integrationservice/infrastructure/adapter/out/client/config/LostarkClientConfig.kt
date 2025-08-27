package io.oikkani.integrationservice.infrastructure.adapter.out.client.config

import io.oikkani.integrationservice.application.port.out.ExceptionNotification
import io.oikkani.integrationservice.infrastructure.adapter.out.client.AuctionClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LostarkClientConfig {

    @Bean
    fun auctionClient(
        @Value("\${lostark.auction.api.key:must-not-null-auction-apikey}") apiKey: String,
        exceptionNotification: ExceptionNotification
    ): AuctionClient {
        return AuctionClient(auctionApiKey, exceptionNotification)
    }


}