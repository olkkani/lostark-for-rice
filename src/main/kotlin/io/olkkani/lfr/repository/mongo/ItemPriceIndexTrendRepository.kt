package io.olkkani.lfr.repository.mongo

import io.olkkani.lfr.entity.mongo.ItemPriceIndexTrend
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ItemPriceIndexTrendRepository: MongoRepository<ItemPriceIndexTrend, ObjectId> {
    fun findByItemCode(itemCode: Int): ItemPriceIndexTrend
}