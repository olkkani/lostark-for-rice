package io.olkkani.lfr.controller

import io.olkkani.lfr.service.AuctionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GemChartRestController (
    private val service: AuctionService
){

    @GetMapping
    fun saveTemp() {

    }
}