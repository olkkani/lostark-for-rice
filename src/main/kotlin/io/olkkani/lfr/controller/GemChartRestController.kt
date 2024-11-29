package io.olkkani.lfr.controller

import io.olkkani.lfr.service.AuctionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/gem-prices/")
class GemChartRestController (
    private val service: AuctionService
){

    @GetMapping
    fun save(): ResponseEntity.BodyBuilder {
        service.saveTodayGemsPrices()
        return ResponseEntity.ok()
    }
}