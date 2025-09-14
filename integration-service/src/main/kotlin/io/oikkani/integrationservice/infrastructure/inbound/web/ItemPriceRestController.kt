package io.oikkani.integrationservice.infrastructure.inbound.web

import io.oikkani.integrationservice.application.port.inbound.AuctionItemPriceUseCase
import io.olkkani.common.dto.contract.CandleChartResponse
import io.oikkani.integrationservice.infrastructure.adapter.inbound.web.dto.ItemPreviousChangeResponse
import io.olkkani.common.api.ItemPreview
import io.olkkani.common.api.ItemPreviewResponse
import io.olkkani.common.dto.contract.CandleChart
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/items")
class ItemPriceRestController(
    private val service: AuctionItemPriceUseCase,
) {

    @GetMapping("/auctions/prices/today")
    fun getAllKindsTodayPrice(): ResponseEntity<List<ItemPreview>> {
//        return ResponseEntity.ok().body(service.getAllKindsTodayPrice())
//    }
        return ResponseEntity.ok().body(null)
    }
    @GetMapping("/{itemCode}/auctions/prices")
    fun getAllPricesByItemCode(@PathVariable itemCode: Int): ResponseEntity<List<CandleChart>> {
//        return ResponseEntity.ok().body(
//            service.getPriceIndexByItemCode(itemCode).map { it.toChartResponse()  }
//        )
        return ResponseEntity.ok().body(null)
    }

    @GetMapping("/{itemCode}/auctions/changes")
    fun getPrevIndexTrendByItemCode(@PathVariable itemCode: Int): ResponseEntity<List<ItemPreviousChangeResponse>> {
//        return ResponseEntity.ok().body(
//            service.getItemPreviousPriceChangesByItemCode(itemCode).map { it.toResponse() }
//        )
        return ResponseEntity.ok().body(null)
    }
}