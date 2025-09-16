package io.oikkani.processorservice.infrastructure.inbound.client

import io.oikkani.processorservice.application.port.inbound.MarketItemPriceSnapshotUseCase
import io.olkkani.common.dto.contract.MarketPriceSnapshot
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("market/items/snapshots")
class MarketPriceSnapshotRestController(
    private val snapshotService: MarketItemPriceSnapshotUseCase,
) {
    @PostMapping()
    fun saveSnapshot(@RequestBody request: MarketPriceSnapshot): ResponseEntity<Unit> {
        snapshotService.saveSnapshotAndUpdateHlcaPrice(request)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping()
    fun deleteAllSnapshotData(): ResponseEntity<Unit>{
        snapshotService.deleteAll()
        return ResponseEntity.noContent().build()
    }
}