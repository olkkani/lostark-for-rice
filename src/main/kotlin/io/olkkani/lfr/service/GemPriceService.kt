package io.olkkani.lfr.service

import io.olkkani.lfr.entity.jpa.AuctionPriceIndex
import io.olkkani.lfr.entity.mongo.RecentPriceIndexTrend

interface GemPriceService {
    fun getAllKindsTodayPrice(): List<AuctionPriceIndex>
    fun getPriceIndexByItemCode(itemCode: Int): List<AuctionPriceIndex>
    fun getPrevTenDaysIndexTrendByItemCode(itemCode: Int): RecentPriceIndexTrend
}