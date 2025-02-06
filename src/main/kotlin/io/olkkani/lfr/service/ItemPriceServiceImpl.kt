package io.olkkani.lfr.service

import io.olkkani.lfr.dto.collectGemInfoList
import io.olkkani.lfr.repository.jpa.ItemPriceIndexRepository
import io.olkkani.lfr.repository.mongo.TodayItemPriceRepository
import org.springframework.stereotype.Service

@Service
class ItemPriceServiceImpl(
    private val repository: ItemPriceIndexRepository,
    private val todayPriceRepository: TodayItemPriceRepository,
    private val todayOpenPriceRepository: TodayItemOpenAndClosePriceRepository,
) : ItemPriceService {

    override fun savaTodayPrice(){
        collectGemInfoList.forEach { gemInfo ->
            val todayItemPrice = todayPriceRepository.findPricesByItemCode(gemInfo.itemCode)
            if(todayItemPrice.isEmpty()){
//                val openPrice = todayOpenPriceRepository.findByItemCode(gemInfo.itemCode).price
//                IQRCalculator(todayItemPrice)



            }
        }
    }
}