package io.olkkani.lfr.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.dto.gemsInfo
import io.olkkani.lfr.entity.ItemPrices
import io.olkkani.lfr.entity.toTempDomains
import io.olkkani.lfr.dto.Item
import io.olkkani.lfr.dto.toDomain
import io.olkkani.lfr.repository.ItemPricesRepository
import io.olkkani.lfr.repository.ItemPricesRepositorySupport
import io.olkkani.lfr.repository.ItemPricesTempRepository
import io.olkkani.lfr.util.LostarkAPIClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional
class AuctionGemService(
    private val repository: ItemPricesRepository,
    private val repositorySupport: ItemPricesRepositorySupport,
    private val tempRepository: ItemPricesTempRepository,
    @Value("\${lostark.api.key}") private val apiKey: String
) {
    private val logger = KotlinLogging.logger {}
    private val apiClient = LostarkAPIClient(apiKey)
    var gems = mutableListOf<Item>()

    init {
        val today = LocalDate.now()
        val gemList = gemsInfo
        gemList.forEach { gemInfo ->
            gems.add(Item(itemCode = gemInfo.first))

            // 오늘자 임시 데이터가 있다면 삽입
            val tempPrices: MutableMap<LocalDateTime, Int> = mutableMapOf()
            tempRepository.findByItemCodeAndRecordedDate(itemCode = gemInfo.first, recordedDate = today)
                .forEach { tempGemPrices ->
                    tempPrices[tempGemPrices.endDate] = tempGemPrices.price
                }
            if (tempPrices.isNotEmpty()) {
                gems.find { gem -> gem.itemCode == gemInfo.first }?.addTodayPrices(tempPrices)
            }
        }
    }

    fun clearTodayPricesRecord() {
        logger.info { "Clear Gems instance ${LocalDateTime.now().toString()}" }
        gems.clear()
        val gemList = gemsInfo
        gemList.forEach { gemInfo ->
            gems.add(Item(itemCode = gemInfo.first))
        }
    }

    fun fetchGemPrices() {
        gemsInfo.forEach { (itemCode, requestData) ->
            apiClient.fetchAuctionItems(requestData).subscribe({ response ->
                if (response != null && response.items.isNotEmpty()) {
                    val responseGemPrices = response.items.associate {
                        it.auctionInfo.endDate to it.auctionInfo.buyPrice
                    }
                    gems.find { it.itemCode == itemCode }?.addTodayPrices(responseGemPrices)
                }
            }, { error ->
                logger.error { "Error fetching $itemCode: ${error.message}" }
            })
        }
    }

    fun fetchGemsPricesSync() {
        gemsInfo.forEach { (itemCode, requestData) ->
            val response = apiClient.fetchAuctionItemsSynchronously(requestData)
            if (response != null && response.items.isNotEmpty()) {
                val responseGemPrices = response.items.associate {
                    it.auctionInfo.endDate to it.auctionInfo.buyPrice
                }
                gems.find { it.itemCode == itemCode }?.addTodayPrices(responseGemPrices)
            }
        }
    }

    fun saveTodayPrices() {
        gems.forEach { gem ->
            if(gem.todayPrices.isNotEmpty()){
                repository.save(gem.toDomain())
            }
        }
    }

    fun saveTodayPricesToTemp() {
        gems.forEach{ gem ->
            if(gem.todayPrices.isNotEmpty()){
                tempRepository.saveAll(gem.toTempDomains())
            }
        }
    }

    fun getTodayTempPricesAndAddPrices() {
        gems.forEach { gem ->
            val prices = mutableMapOf<LocalDateTime, Int>()
            tempRepository.findByItemCodeAndRecordedDate(gem.itemCode, gem.time).let { tempItemPrices ->
                tempItemPrices.forEach { tempItemPrice ->
                    prices[tempItemPrice.endDate] = tempItemPrice.price
                }
            }
            if(prices.isNotEmpty()) {
                gem.addTodayPrices(prices)
            }
        }
    }

    fun getPricesByItemCode(itemCode: Int): List<ItemPrices>{
        val prices = repositorySupport.findOldAllByItemCode(itemCode)
        gems.find { it.itemCode == itemCode }?.let { prices.add(it.toDomain()) }
        return prices
    }

    fun getAllKindsTodayPrice(): MutableList<Item> {
        return gems
    }
}