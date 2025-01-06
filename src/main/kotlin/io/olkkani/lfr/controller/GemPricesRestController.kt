package io.olkkani.lfr.controller

import io.olkkani.lfr.dto.CandleChartResponse
import io.olkkani.lfr.dto.TodayPriceResponse
import io.olkkani.lfr.dto.toResponse
import io.olkkani.lfr.service.AuctionGemService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/items")
class GemPricesRestController(
    private val service: AuctionGemService,
) {

    @GetMapping("/prices/today")
    fun getAllKindsTodayPrice() {
        val prices: MutableList<TodayPriceResponse> = mutableListOf()
        ResponseEntity.ok().body(
            service.getAllKindsTodayPrice().map { gem ->
                prices.add(
                    TodayPriceResponse(
                        itemCode = gem.itemCode,
                        price = gem.toResponse()
                    )
                )
            }
        )
    }
    @GetMapping("/{itemCode}/prices")
    fun getAllPricesByItemCode(@PathVariable itemCode: Int): ResponseEntity<List<CandleChartResponse>> {
        return ResponseEntity.ok().body(
            service.getPricesByItemCode(itemCode).map { it.toResponse() }
        )
    }
}