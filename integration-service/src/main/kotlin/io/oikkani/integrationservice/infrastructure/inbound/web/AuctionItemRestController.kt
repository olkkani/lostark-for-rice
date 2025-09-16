package io.oikkani.integrationservice.infrastructure.inbound.web

import io.oikkani.integrationservice.application.port.inbound.AuctionItemPriceUseCase
import io.olkkani.common.api.ItemPreview
import io.olkkani.common.dto.contract.CandleChart
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/items")
class AuctionItemRestController(
    private val auctionService: AuctionItemPriceUseCase,
) {

    @GetMapping("/auctions/prices/today")
    suspend fun getAllTodayItemsPreview(): ResponseEntity<List<ItemPreview>> {
        return ResponseEntity.ok().body(auctionService.getAllTodayItemsPreview())
    }
    @GetMapping("/{itemCode}/auctions/prices")
    suspend fun findOhlcPriceChartByItemCode(@PathVariable itemCode: Int): ResponseEntity<List<CandleChart>> {
        return ResponseEntity.ok().body(auctionService.findOhlcPriceChartByItemCode(itemCode))
    }

}