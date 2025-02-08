package io.olkkani.lfr.service

interface AuctionSchedulerService {
    suspend fun fetchPriceAndInsertOpenPrice()
    suspend fun fetchPriceAndUpdateClosePrice()
    suspend fun fetchPriceAndUpdateLowAndHighPrice()
    fun clearOldPriceRecord()
    fun calculateGapTodayItemPrice()
}