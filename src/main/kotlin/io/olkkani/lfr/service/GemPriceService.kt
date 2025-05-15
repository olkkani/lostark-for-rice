package io.olkkani.lfr.service

import io.olkkani.lfr.entity.jpa.AuctionItemOhlcPrice
import io.olkkani.lfr.entity.mongo.RecentPriceIndexTrend

interface GemPriceService {
    fun getAllKindsTodayPrice(): List<AuctionItemOhlcPrice>
    fun getPriceIndexByItemCode(itemCode: Int): List<AuctionItemOhlcPrice>
    fun getPrevTenDaysIndexTrendByItemCode(itemCode: Int): RecentPriceIndexTrend
}