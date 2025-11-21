package io.oikkani.integrationservice.application.port.inbound

interface MarketItemPriceUseCase {
    fun getAllItemsTodayPrice()
    fun getItemPriceByItemCode(itemCode: Int)
}