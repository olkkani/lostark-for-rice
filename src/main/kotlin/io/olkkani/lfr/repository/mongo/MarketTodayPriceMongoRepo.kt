package io.olkkani.lfr.repository.mongo

import io.olkkani.lfr.entity.mongo.MarketTodayPrice
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

fun interface MarketPriceProjection {
    fun getPrice(): Int
}

@Repository
interface MarketTodayPriceMongoRepo: MongoRepository<MarketTodayPrice, ObjectId>, MarketTodayPriceTemplateRepo {
    @Query("{ 'itemCode': :#{#itemCode} }")
    fun findPricesByItemCode(itemCode: Int): List<MarketPriceProjection>
}