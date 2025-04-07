package io.olkkani.lfr.repository.mongo

import io.olkkani.lfr.entity.mongo.MarketTodayPrice
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

fun interface MarketTodayPriceTemplateRepo {
    fun saveIfNotExists(marketTodayPrice: MarketTodayPrice)
}

class MarketTodayPriceTemplateRepoImpl (
    private val mongoTemplate: MongoTemplate
): MarketTodayPriceTemplateRepo {
    override fun saveIfNotExists(marketTodayPrice: MarketTodayPrice) {
        val query = Query(
            Criteria.where("price").`is`(marketTodayPrice.price)
        )
        if (!mongoTemplate.exists(query, MarketTodayPrice::class.java)) {
            mongoTemplate.insert(marketTodayPrice)
        }
    }

}