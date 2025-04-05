package io.olkkani.lfr.repository.mongo

import io.olkkani.lfr.entity.mongo.RecentPriceIndexTrend
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface RecentPriceIndexTrendMongoRepo: MongoRepository<RecentPriceIndexTrend, ObjectId> {
    fun findByItemCode(itemCode: Int): RecentPriceIndexTrend?
}