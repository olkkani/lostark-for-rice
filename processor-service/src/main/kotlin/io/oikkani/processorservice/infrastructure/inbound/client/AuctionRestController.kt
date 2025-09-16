package io.oikkani.processorservice.infrastructure.inbound.client

import io.oikkani.processorservice.application.service.AuctionService
import io.olkkani.common.api.ItemPreview
import io.olkkani.common.dto.contract.CandleChart
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("auction/items")
class AuctionRestController(
    private val service: AuctionService,
) {

    @GetMapping("/preview/today")
    fun getTodayItemsPreview(): ResponseEntity<List<ItemPreview>?> {
        return ResponseEntity.ok().body(service.getAllTodayItems().map { it.toPreview() })
    }

    @GetMapping("/{itemCode}/ohlc")
    fun findOhlcPriceChartByItemCode(@PathVariable itemCode: Int): ResponseEntity<List<CandleChart>?> {
        return ResponseEntity.ok().body(service.findAllOhlcByItemCode(itemCode).map { it.toChart()})
    }
}