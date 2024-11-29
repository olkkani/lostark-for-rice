package io.olkkani.lfr.controller

import io.olkkani.lfr.service.AuctionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/gem-prices")
class GemChartRestController (
    private val service: AuctionService
){

    @GetMapping("/save")
    fun save(): ResponseEntity<String> {
        service.saveTodayGemsPrices()
        return ResponseEntity.ok().body("ok")
    }
}