package io.oikkani.integrationservice.application.service

import io.oikkani.integrationservice.application.port.inbound.MarketFetchUseCase
import org.springframework.stereotype.Service

@Service
class MarketMaterialService: MarketFetchUseCase {

    override suspend fun fetchAndSendPriceData() {
        TODO("Not yet implemented")
    }

}