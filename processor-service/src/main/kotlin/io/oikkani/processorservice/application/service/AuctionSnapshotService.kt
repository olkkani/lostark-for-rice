package io.oikkani.processorservice.application.service

import io.oikkani.processorservice.application.port.inbound.AuctionSnapshotUseCase
import io.oikkani.processorservice.application.port.outbound.AuctionItemOhlcPriceRepositoryPort
import io.oikkani.processorservice.application.port.outbound.AuctionItemPriceSnapshotRepositoryPort
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyAuctionItemOhlcPrice
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.toEntityList
import io.olkkani.common.dto.contract.AuctionPriceSnapshot
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class AuctionSnapshotService(
    private val snapshotRepo: AuctionItemPriceSnapshotRepositoryPort,
    private val ohlcRepo: AuctionItemOhlcPriceRepositoryPort,
) : AuctionSnapshotUseCase {


    @Transactional
    override fun saveSnapshotAndUpdateHlcPrice(auctionPriceSnapshot: AuctionPriceSnapshot) {
        snapshotRepo.saveAllNotExists(auctionPriceSnapshot.toEntityList())

        // TODO 이상치 제거 로직 추가
        // Update HLC Price
        val priceSnapshot = snapshotRepo.findAllByItemCode(auctionPriceSnapshot.itemCode)
        val todayLowPrice = priceSnapshot.minOf { it.price }
        val todayHighPrice = priceSnapshot.maxOf { it.price }
        val todayClosePrice = auctionPriceSnapshot.prices.minOf { it.price }
        val today = LocalDate.now()
        ohlcRepo.findByItemCodeAndRecordedDate(itemCode = auctionPriceSnapshot.itemCode, recordedDate = today)
            ?.apply {
                // 기존 데이터가 존재하면 오늘자 데이터를 수정
                highPrice = todayHighPrice
                lowPrice = todayLowPrice
                closePrice = todayClosePrice
                ohlcRepo.save(this)
            }?:run {
                // 데이터가 존재하지 않으면 신규 데이터를 생성 및 삽입
                ohlcRepo.save(
                    DailyAuctionItemOhlcPrice(
                        itemCode = auctionPriceSnapshot.itemCode,
                        recordedDate = today,
                        openPrice = todayLowPrice,
                        highPrice = todayHighPrice,
                        lowPrice = todayLowPrice,
                        closePrice = todayClosePrice
                    )
                )
            }


    }

    override fun deleteAll() {
        snapshotRepo.deleteAll()
    }

}