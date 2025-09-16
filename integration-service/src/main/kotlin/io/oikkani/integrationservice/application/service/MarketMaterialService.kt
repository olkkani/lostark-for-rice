package io.oikkani.integrationservice.application.service

import io.oikkani.integrationservice.application.port.inbound.MarketMaterialUseCase
import org.springframework.stereotype.Service

@Service
class MarketMaterialService: MarketMaterialUseCase {

    override suspend fun fetchAndSendPriceData() {
        TODO("Not yet implemented")
    }

}