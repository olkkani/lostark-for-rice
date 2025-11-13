package io.oikkani.processorservice.application.service

import io.oikkani.processorservice.application.port.inbound.MarketSnapshotUseCase
import io.oikkani.processorservice.application.port.outbound.MarketItemOhlcaRepositoryPort
import io.oikkani.processorservice.application.port.outbound.MarketItemPriceSnapshotRepositoryPort
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyMarketItemOhlcaPrice
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.toEntity
import io.olkkani.common.dto.contract.MarketPrice
import io.olkkani.common.dto.contract.MarketPriceSnapshot
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MarketSnapshotService(
    private val snapshotRepository: MarketItemPriceSnapshotRepositoryPort,
    private val ohlcaRepository: MarketItemOhlcaRepositoryPort,
) : MarketSnapshotUseCase {

    override fun saveSnapshotAndUpdateHlcaPrice(snapshot: MarketPriceSnapshot) {
        snapshotRepository.saveAllNotExists(snapshot.prices.map { it.toEntity() })
        updateOhlcPrice(snapshot.prices)
        if (snapshot.isUpdateYesterdayAvgPrice) {
            updateYesterdayAvgPrices(snapshot.prices)
        }
    }

    override fun deleteAll() {
        snapshotRepository.deleteAll()
    }

    private fun updateOhlcPrice(marketPrices: List<MarketPrice>) {
        val today = LocalDate.now()
        val ohlcPrices = ohlcaRepository.findAllByRecordedDate(today)

        marketPrices.forEach { marketPrice ->
            val closePrice = marketPrice.price

            ohlcPrices.find { it.itemCode == marketPrice.itemCode }
                ?.apply {
                    this.highPrice = highPrice.coerceAtLeast(marketPrice.price)
                    // TODO 이상치 제거
                    this.lowPrice = lowPrice.coerceAtMost(marketPrice.price)
                    this.closePrice = closePrice
                    ohlcaRepository.save(this)
                } ?: run {
                val todayOhlc = DailyMarketItemOhlcaPrice(
                    itemCode = marketPrice.itemCode,
                    recordedDate = today,
                    openPrice = marketPrice.price,
                    highPrice = marketPrice.price,
                    lowPrice = marketPrice.price,
                    closePrice = marketPrice.price
                )
                ohlcaRepository.save(todayOhlc)
            }


        }
    }

    private fun updateYesterdayAvgPrices(marketPrices: List<MarketPrice>) {
        val yesterday = LocalDate.now().minusDays(1)
        val yesterdayOhlc = ohlcaRepository.findAllByRecordedDate(yesterday)

        yesterdayOhlc.map { ohlc ->
            marketPrices.find { it.itemCode == ohlc.itemCode }
                ?.apply {
                    ohlc.avgPrice = this.yDateAvgPrice
                    ohlcaRepository.save(ohlc)
                }
        }
    }
}