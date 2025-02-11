package io.olkkani.lfr.service

interface AuctionSchedulerService {
    suspend fun fetchPriceAndInsertOpenPrice()
    suspend fun fetchPriceAndUpdateClosePrice()
    suspend fun fetchPriceAndUpdatePrice()
    fun clearOldPriceRecord()
    fun calculateGapTodayItemPrice()
}