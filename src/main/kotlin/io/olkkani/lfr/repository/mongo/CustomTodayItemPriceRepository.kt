package io.olkkani.lfr.repository.mongo

import io.olkkani.lfr.entity.mongo.TodayItemPrice
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

fun interface TodayItemPricesTemplateRepository {
    fun saveIfNotExists(todayPrices: List<TodayItemPrice>)
}

class TodayItemPricesTemplateRepositoryImpl (
    private val mongoTemplate: MongoTemplate
): TodayItemPricesTemplateRepository {
    override fun saveIfNotExists(todayPrices: List<TodayItemPrice>) {
        val bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, TodayItemPrice::class.java)

        todayPrices.forEach { data ->
            val query = Query(
                Criteria.where("itemCode").`is`(data.itemCode)
                    .and("endDate").`is`(data.endDate)
            )

            if (!mongoTemplate.exists(query, TodayItemPrice::class.java)) {
                bulkOps.insert(data)
            }
        }
        bulkOps.execute()
    }
}