package io.olkkani.lfr.controller

import io.olkkani.lfr.dto.CandleChartResponse
import io.olkkani.lfr.dto.toResponse
import io.olkkani.lfr.service.AuctionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/gem-prices")
class GemPricesRestController (
    private val service: AuctionService
){
    @GetMapping("/{itemCode}")
    fun getAllByItemCode(
        @PathVariable("itemCode") itemCode: Int
    ): ResponseEntity<List<CandleChartResponse>> {
        return ResponseEntity.ok().body(service.getAllGemsPricesByItemCode(itemCode).map { it.toResponse() })
    }

    @GetMapping("/find")
    fun execFetchGemPrices(): ResponseEntity<List<CandleChartResponse>> {
        service.fetchGemsPrices()
        return ResponseEntity.ok().body(service.getAllGemsPricesByItemCode(65021100).map { it.toResponse() })
    }
    @GetMapping("/find-open")
    fun execFetchGemOpenPrices(): ResponseEntity.BodyBuilder {
        service.fetchGemsOpenPrice()
        return ResponseEntity.ok()
    }

    @GetMapping("/save")
    fun save(): ResponseEntity<String> {
        service.saveTodayGemsPrices()
        return ResponseEntity.ok().body("ok")
    }

}