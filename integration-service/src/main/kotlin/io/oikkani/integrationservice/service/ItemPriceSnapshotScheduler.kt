//package io.oikkani.integrationservice.service
//
//import io.olkkani.lfr.repository.AuctionItemPriceSnapshotRepo
//import io.olkkani.lfr.repository.ItemPreviousPriceChangeRepo
//import io.olkkani.lfr.repository.MarketItemPriceSnapshotRepo
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Transactional
//import java.time.LocalDate
//
//@Service
//class ItemPriceSnapshotScheduler(
//    private val itemPreviousPriceChangeRepo: ItemPreviousPriceChangeRepo,
//    private val auctionItemPriceSnapshotRepo: AuctionItemPriceSnapshotRepo,
//    private val marketItemPriceSnapshotRepo: MarketItemPriceSnapshotRepo,
//) {
//
//    @Transactional
//    fun deleteSnapshotData() {
//        // Reset Table Data
//        auctionItemPriceSnapshotRepo.truncateTable()
//        marketItemPriceSnapshotRepo.truncateTable()
//        // Delete Ten days old snapshot
//        val prevTenDays = LocalDate.now().minusDays(10)
//        itemPreviousPriceChangeRepo.deleteAllByRecordedDate(prevTenDays)
//    }
//}