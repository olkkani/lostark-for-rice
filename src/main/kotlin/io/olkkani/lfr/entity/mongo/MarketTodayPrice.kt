package io.olkkani.lfr.entity.mongo

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "marketTodayPrice")
class MarketTodayPrice (
    @Id
    val id: ObjectId? = null,
    val itemCode: Int,
    val price: Int,
)