package io.olkkani.lfr.repository.mongo

import io.olkkani.lfr.entity.mongo.AuctionTodayPrice
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

fun interface AuctionTodayPriceTemplateRepo {
    fun saveIfNotExists(todayPrices: List<AuctionTodayPrice>)
}

class AuctionTodayPriceTemplateRepoImpl (
    private val mongoTemplate: MongoTemplate
): AuctionTodayPriceTemplateRepo {
    override fun saveIfNotExists(todayPrices: List<AuctionTodayPrice>) {
        val inserts = todayPrices.filter { data ->
            val query = Query(
                Criteria.where("itemCode").`is`(data.itemCode)
                    .and("endDate").`is`(data.endDate)
            )
            !mongoTemplate.exists(query, AuctionTodayPrice::class.java)
        }

        if (inserts.isNotEmpty()) {
            mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, AuctionTodayPrice::class.java)
                .insert(inserts)
                .execute()
        }
    }
}