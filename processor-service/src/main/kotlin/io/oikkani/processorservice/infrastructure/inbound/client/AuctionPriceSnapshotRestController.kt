package io.oikkani.processorservice.infrastructure.inbound.client

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("auction/items/snapshots")
class AuctionPriceSnapshotRestController(

) {
    @PostMapping()
    fun saveSnapshot(){
        //todo
    }

}