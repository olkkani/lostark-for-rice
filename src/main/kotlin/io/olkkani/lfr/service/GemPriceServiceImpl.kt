package io.olkkani.lfr.service

import io.olkkani.lfr.dto.ItemTodayPriceDTO
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
): GemPriceService {
    override fun getAllKindsTodayPrice(): List<ItemTodayPriceDTO> {
        return indexRepository.findAllByRecordedDate(LocalDate.now()).map{ todayIndex ->
            ItemTodayPriceDTO(
                itemCode = todayIndex.itemCode,
                price = todayIndex.closePrice,
                priceGap = todayIndex.closePrice - todayIndex.openPrice,
                priceGapRate = ((todayIndex.closePrice*1000) / (todayIndex.openPrice*1000)).toDouble() / 1000
            )
        }
    }

    override fun getPriceIndexByItemCode(itemCode: Int): List<ItemPriceIndex> {
        return indexRepository.findAllByItemCode(itemCode)
    }

    override fun getPrevTenDaysIndexTrendByItemCode(itemCode: Int): ItemPriceIndexTrend {
        return indexTrendRepository.findByItemCode(itemCode)
    }
}