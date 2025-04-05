package io.olkkani.lfr.controller

import io.olkkani.lfr.dto.CandleChartResponse
import io.olkkani.lfr.dto.ItemPreview
import io.olkkani.lfr.entity.jpa.toChartResponse
import io.olkkani.lfr.entity.jpa.toPreviewResponse
import io.olkkani.lfr.entity.mongo.TodayPriceGap
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
    fun getAllKindsTodayPrice(): ResponseEntity<List<ItemPreview>> {
        return ResponseEntity.ok().body(service.getAllKindsTodayPrice().map { it.toPreviewResponse()  })
    }

    @GetMapping("/{itemCode}/prices")
    fun getAllPricesByItemCode(@PathVariable itemCode: Int): ResponseEntity<List<CandleChartResponse>> {
        return ResponseEntity.ok().body(
            service.getPriceIndexByItemCode(itemCode).map { it.toChartResponse()  }
        )
    }

    @GetMapping("/{itemCode}/trend")
    fun getPrevIndexTrendByItemCode(@PathVariable itemCode: Int): ResponseEntity<List<TodayPriceGap>> {
        return ResponseEntity.ok().body(
            service.getPrevTenDaysIndexTrendByItemCode(itemCode).toResponse()
        )
    }
}