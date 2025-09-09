package io.oikkani.processorservice.infrastructure.inbound

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("")
class AuctionItemPriceRestController {


    @PostMapping()
    fun updatePrice() {

    }

    @DeleteMapping()
    fun deleteAllSnapshotData() {

    }

}