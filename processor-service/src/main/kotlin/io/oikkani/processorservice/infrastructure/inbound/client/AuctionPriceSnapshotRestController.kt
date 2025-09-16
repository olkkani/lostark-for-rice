package io.oikkani.processorservice.infrastructure.inbound.client

import io.oikkani.processorservice.application.port.inbound.AuctionSnapshotUseCase
import io.olkkani.common.dto.contract.AuctionPriceSnapshot
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("auction/items/snapshots")
class AuctionPriceSnapshotRestController(
    private val snapshotService : AuctionSnapshotUseCase
) {
    @PostMapping()
    fun saveSnapshot(@RequestBody request: AuctionPriceSnapshot): ResponseEntity<Unit> {
        snapshotService.saveSnapshotAndUpdateHlcPrice(request)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping()
    fun deleteAllSnapshotData(): ResponseEntity<Unit>{
            snapshotService.deleteAll()
            return ResponseEntity.noContent().build()
        //todo globalexception handler and return custom error message
    }
}