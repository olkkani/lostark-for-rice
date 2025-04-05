package io.olkkani.lfr.service

import io.olkkani.lfr.entity.jpa.AuctionPriceIndex
import io.olkkani.lfr.entity.mongo.RecentPriceIndexTrend
import io.olkkani.lfr.repository.jpa.AuctionPriceIndexRepo
import io.olkkani.lfr.repository.mongo.RecentPriceIndexTrendMongoRepo
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class GemPriceServiceImpl(
    private val indexRepository: AuctionPriceIndexRepo,
    private val indexTrendRepository: RecentPriceIndexTrendMongoRepo,
) : GemPriceService {
    override fun getAllKindsTodayPrice(): List<AuctionPriceIndex> {
        return indexRepository.findAllByRecordedDate(LocalDate.now())
    }

    override fun getPriceIndexByItemCode(itemCode: Int): List<AuctionPriceIndex> {
        return indexRepository.findAllByItemCodeOrderByRecordedDateAsc(itemCode)
    }

    override fun getPrevTenDaysIndexTrendByItemCode(itemCode: Int): RecentPriceIndexTrend {
        return indexTrendRepository.findByItemCode(itemCode) ?: RecentPriceIndexTrend(itemCode = itemCode)
    }
}