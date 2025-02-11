package io.olkkani.lfr.service

import io.olkkani.lfr.entity.jpa.ItemPriceIndex
import io.olkkani.lfr.entity.mongo.ItemPriceIndexTrend
import io.olkkani.lfr.repository.jpa.ItemPriceIndexRepository
import io.olkkani.lfr.repository.mongo.ItemPriceIndexTrendRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class GemPriceServiceImpl(
    private val indexRepository: ItemPriceIndexRepository,
    private val indexTrendRepository: ItemPriceIndexTrendRepository,
) : GemPriceService {
    override fun getAllKindsTodayPrice(): List<ItemPriceIndex> {
        return indexRepository.findAllByRecordedDate(LocalDate.now())
    }

    override fun getPriceIndexByItemCode(itemCode: Int): List<ItemPriceIndex> {
        return indexRepository.findAllByItemCodeOrderByRecordedDateAsc(itemCode)
    }

    override fun getPrevTenDaysIndexTrendByItemCode(itemCode: Int): ItemPriceIndexTrend {
        return indexTrendRepository.findByItemCode(itemCode) ?: ItemPriceIndexTrend(itemCode = itemCode)
    }
}