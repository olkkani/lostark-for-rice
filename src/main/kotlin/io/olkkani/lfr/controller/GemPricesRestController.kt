package io.olkkani.lfr.controller

import io.olkkani.lfr.dto.CandleChartResponse
import io.olkkani.lfr.dto.ItemTodayPriceDTO
import io.olkkani.lfr.dto.toResponse
import io.olkkani.lfr.entity.mongo.PriceRecord
import io.olkkani.lfr.entity.mongo.toResponse
import io.olkkani.lfr.service.GemPriceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/items")
class GemPricesRestController(
    private val service: GemPriceService,
) {

    @GetMapping("/prices/today")
    fun getAllKindsTodayPrice(): ResponseEntity<List<ItemTodayPriceDTO>> {
        return ResponseEntity.ok().body(service.getAllKindsTodayPrice())
    }

    @GetMapping("/{itemCode}/prices")
    fun getAllPricesByItemCode(@PathVariable itemCode: Int): ResponseEntity<List<CandleChartResponse>> {
        return ResponseEntity.ok().body(
            service.getPriceIndexByItemCode(itemCode).map { it.toResponse()  }
        )
    }

    @GetMapping("/{itemCode}/trend")
    fun getPrevIndexTrendByItemCode(@PathVariable itemCode: Int): ResponseEntity<List<PriceRecord>> {
        return ResponseEntity.ok().body(
            service.getPrevTenDaysIndexTrendByItemCode(itemCode).toResponse()
        )
    }
}