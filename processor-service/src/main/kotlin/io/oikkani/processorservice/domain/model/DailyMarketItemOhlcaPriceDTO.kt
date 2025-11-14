package io.oikkani.processorservice.domain.model

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyMarketItemOhlcaPriceEntity
import java.time.LocalDate

class DailyMarketItemOhlcaPriceDTO(
    val id: Long? = null,
    val itemCode: Int,
    val recordedDate: LocalDate,
    var openPrice: Int,
    var highPrice: Int,
    var lowPrice: Int,
    var closePrice: Int,
    var avgPrice: Float = 0F,
) {
    fun toEntity() = DailyMarketItemOhlcaPriceEntity(
        id = id,
        itemCode = itemCode,
        recordedDate = recordedDate,
        openPrice = openPrice,
        highPrice = highPrice,
        lowPrice = lowPrice,
        closePrice = closePrice,
        avgPrice = avgPrice,
    )
}