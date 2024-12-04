package io.olkkani.lfr.config

import io.olkkani.lfr.service.AuctionService
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Component


@Component
class ApplicationStartup (
    private val service: AuctionService
){
    @PreDestroy
    fun onDestroy(){
        service.saveTodayGemsPricesTemp()
    }
}

