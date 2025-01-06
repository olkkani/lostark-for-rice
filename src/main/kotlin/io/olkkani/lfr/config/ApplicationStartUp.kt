package io.olkkani.lfr.config

import io.olkkani.lfr.service.AuctionGemService
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Component


@Component
class ApplicationStartup (
    private val service: AuctionGemService
){
    @PreDestroy
    fun onDestroy(){
        service.saveTodayPricesToTemp()
    }
}

