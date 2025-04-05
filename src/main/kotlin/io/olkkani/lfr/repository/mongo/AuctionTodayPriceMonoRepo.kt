package io.olkkani.lfr.repository.mongo

import io.olkkani.lfr.entity.mongo.AuctionTodayPrice
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

fun interface PriceProjection {
    fun getPrice(): Int
}

@Repository
interface TodayItemPriceRepository: MongoRepository<AuctionTodayPrice, ObjectId>, AuctionTodayPriceTemplateRepo {
    @Query("{ 'itemCode': :#{#itemCode} }")
    fun findPricesByItemCode(itemCode: Int): List<PriceProjection>
}