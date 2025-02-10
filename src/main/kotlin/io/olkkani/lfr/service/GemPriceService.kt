package io.olkkani.lfr.service

import io.olkkani.lfr.dto.ItemTodayPriceDTO
import io.olkkani.lfr.entity.jpa.ItemPriceIndex
import io.olkkani.lfr.entity.mongo.ItemPriceIndexTrend

interface GemPriceService {
    fun getAllKindsTodayPrice(): List<ItemTodayPriceDTO>
    fun getPriceIndexByItemCode(itemCode: Int): List<ItemPriceIndex>
    fun getPrevTenDaysIndexTrendByItemCode(itemCode: Int): ItemPriceIndexTrend
}