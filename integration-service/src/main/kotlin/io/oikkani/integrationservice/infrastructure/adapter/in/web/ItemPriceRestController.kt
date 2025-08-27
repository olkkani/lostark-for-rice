package io.oikkani.integrationservice.infrastructure.adapter.`in`.web

import io.olkkani.lfr.controller.dto.CandleChartResponse
import io.olkkani.lfr.controller.dto.ItemPreviewDTO
import io.olkkani.lfr.controller.dto.ItemPreviousChangeResponse
import io.olkkani.lfr.service.AuctionItemPriceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/items")
class ItemPriceRestController(
    private val service: AuctionItemPriceService,
) {

    @GetMapping("/auctions/prices/today")
    fun getAllKindsTodayPrice(): ResponseEntity<List<ItemPreviewDTO>> {
        return ResponseEntity.ok().body(service.getAllKindsTodayPrice().map { it.toPreviewResponse()  })
    }

    @GetMapping("/{itemCode}/auctions/prices")
    fun getAllPricesByItemCode(@PathVariable itemCode: Int): ResponseEntity<List<CandleChartResponse>> {
        return ResponseEntity.ok().body(
            service.getPriceIndexByItemCode(itemCode).map { it.toChartResponse()  }
        )
    }

    @GetMapping("/{itemCode}/auctions/changes")
    fun getPrevIndexTrendByItemCode(@PathVariable itemCode: Int): ResponseEntity<List<ItemPreviousChangeResponse>> {
        return ResponseEntity.ok().body(
            service.getItemPreviousPriceChangesByItemCode(itemCode).map { it.toResponse() }
        )
    }
}