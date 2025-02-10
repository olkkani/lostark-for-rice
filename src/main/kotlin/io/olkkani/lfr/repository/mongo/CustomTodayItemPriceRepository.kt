package io.olkkani.lfr.repository.mongo

import io.olkkani.lfr.entity.mongo.TodayItemPrice
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

fun interface CustomTodayItemPriceRepository {
    fun saveIfNotExists(todayPrices: List<TodayItemPrice>)
}

class CustomTodayItemPriceRepositoryImpl (
    private val mongoTemplate: MongoTemplate
): CustomTodayItemPriceRepository {
    override fun saveIfNotExists(todayPrices: List<TodayItemPrice>) {
        val inserts = todayPrices.filter { data ->
            val query = Query(
                Criteria.where("itemCode").`is`(data.itemCode)
                    .and("endDate").`is`(data.endDate)
            )
            !mongoTemplate.exists(query, TodayItemPrice::class.java)
        }

        if (inserts.isNotEmpty()) {
            mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, TodayItemPrice::class.java)
                .insert(inserts)
                .execute()
        }
    }
}