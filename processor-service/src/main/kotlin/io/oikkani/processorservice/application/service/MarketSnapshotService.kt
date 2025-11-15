package io.oikkani.processorservice.application.service

import io.oikkani.processorservice.application.port.inbound.MarketSnapshotUseCase
import io.oikkani.processorservice.application.port.outbound.MarketItemOhlcaRepositoryPort
import io.oikkani.processorservice.application.port.outbound.MarketItemPriceSnapshotRepositoryPort
import io.oikkani.processorservice.domain.model.DailyMarketItemOhlcaPriceDTO
import io.oikkani.processorservice.domain.model.toSnapshot
import io.olkkani.common.dto.contract.MarketItemPrice
import io.olkkani.common.dto.contract.MarketPriceSnapshotRequest
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MarketSnapshotService(
    private val snapshotRepository: MarketItemPriceSnapshotRepositoryPort,
    private val ohlcaRepository: MarketItemOhlcaRepositoryPort,
) : MarketSnapshotUseCase {

    override fun saveSnapshotAndUpdateHlcaPrice(snapshotRequest: MarketPriceSnapshotRequest) {
        snapshotRepository.saveAllNotExists(snapshotRequest.prices.map { it.toSnapshot() })
        // update today ohlc price
        updateOhlcPrice(snapshotRequest.prices)
        // update yesterday avg price when open scheduler
        if (snapshotRequest.isUpdateYesterdayAvgPrice) {
            updateYesterdayAvgPrices(snapshotRequest.prices)
        }
    }

    override fun deleteAll() {
        snapshotRepository.deleteAll()
    }

    private fun updateOhlcPrice(marketItemPrices: List<MarketItemPrice>) {
        val today = LocalDate.now()
        val ohlcPrices = ohlcaRepository.findAllByRecordedDate(today)

        marketItemPrices.forEach { marketPrice ->
            val closePrice = marketPrice.price

            ohlcPrices.find { it.itemCode == marketPrice.itemCode }
                ?.apply {
                    this.highPrice = highPrice.coerceAtLeast(marketPrice.price)
                    // TODO 이상치 제거
                    this.lowPrice = lowPrice.coerceAtMost(marketPrice.price)
                    this.closePrice = closePrice
                    ohlcaRepository.save(this)
                } ?: run {
                val todayOhlc = DailyMarketItemOhlcaPriceDTO(
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

    private fun updateYesterdayAvgPrices(marketItemPrices: List<MarketItemPrice>) {
        val yesterday = LocalDate.now().minusDays(1)
        val yesterdayOhlc = ohlcaRepository.findAllByRecordedDate(yesterday)

        yesterdayOhlc.map { ohlc ->
            marketItemPrices.find { it.itemCode == ohlc.itemCode }
                ?.apply {
                    ohlc.avgPrice = this.yDateAvgPrice
                    ohlcaRepository.save(ohlc)
                }
        }
    }
}