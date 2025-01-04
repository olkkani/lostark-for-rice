package io.olkkani.lfr.controller

import io.olkkani.lfr.dto.CandleChartResponse
import io.olkkani.lfr.dto.toResponse
import io.olkkani.lfr.service.AuctionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/gem-prices")
class GemPricesRestController(
    private val service: AuctionService
) {
    @GetMapping
    fun getAll(): ResponseEntity<MutableMap<Int, List<CandleChartResponse>>> {
        return ResponseEntity.ok().body(
            service.getAllPrices().mapValues { (_, prices) ->
                prices.map { it.toResponse() }
            }.toMutableMap()
        )
    }
}