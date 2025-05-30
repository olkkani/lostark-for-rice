package io.olkkani.lfr.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/alerts")
class PriceAlertRestController {

    fun createAlert(){}

    fun getAlertByItemCode(){}

    fun getAllAlerts(){}

    fun deleteAlertByItemCode(){}

    fun deleteAllAlerts(){}
}