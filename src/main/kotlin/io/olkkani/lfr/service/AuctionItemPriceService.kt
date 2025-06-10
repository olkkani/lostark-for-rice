package io.olkkani.lfr.service

import io.olkkani.lfr.repository.DailyAuctionItemOhlcPriceRepo
import io.olkkani.lfr.repository.ItemPreviousPriceChangeRepo
import io.olkkani.lfr.repository.entity.DailyAuctionItemOhlcPrice
import io.olkkani.lfr.repository.entity.ItemPreviousPriceChange
import org.springframework.stereotype.Service
import java.time.LocalDate

interface AuctionItemPriceService {
    fun getAllKindsTodayPrice(): List<DailyAuctionItemOhlcPrice>
    fun getPriceIndexByItemCode(itemCode: Int): List<DailyAuctionItemOhlcPrice>
    fun getItemPreviousPriceChangesByItemCode(itemCode: Int): List<ItemPreviousPriceChange>
}

@Service
class AuctionItemPriceServiceImpl(
    private val itemOhlcPriceRepo: DailyAuctionItemOhlcPriceRepo,
    private val itemPreviousPriceChangeRepo: ItemPreviousPriceChangeRepo,
) : AuctionItemPriceService {
    override fun getAllKindsTodayPrice(): List<DailyAuctionItemOhlcPrice> {
        return itemOhlcPriceRepo.findAllByRecordedDate(LocalDate.now())
    }

    override fun getPriceIndexByItemCode(itemCode: Int): List<DailyAuctionItemOhlcPrice> {
        return itemOhlcPriceRepo.findAllByItemCodeOrderByRecordedDateAsc(itemCode)
    }

    override fun getItemPreviousPriceChangesByItemCode(itemCode: Int): List<ItemPreviousPriceChange> {
        return itemPreviousPriceChangeRepo.findAllByItemCodeOrderByRecordedDateAsc(itemCode)
    }
}