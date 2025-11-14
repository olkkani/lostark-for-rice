package io.oikkani.processorservice.infrastructure.inbound.client

import io.oikkani.processorservice.application.port.inbound.MarketSnapshotUseCase
import io.olkkani.common.dto.contract.MarketPriceSnapshotRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("market/items/snapshots")
class MarketPriceSnapshotRestController(
    private val snapshotService: MarketSnapshotUseCase,
) {
    @PostMapping()
    fun saveSnapshot(@RequestBody snapshotRequest : MarketPriceSnapshotRequest): ResponseEntity<Unit> {
        snapshotService.saveSnapshotAndUpdateHlcaPrice(snapshotRequest)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping()
    fun deleteAllSnapshotData(): ResponseEntity<Unit>{
        snapshotService.deleteAll()
        return ResponseEntity.noContent().build()
    }
}