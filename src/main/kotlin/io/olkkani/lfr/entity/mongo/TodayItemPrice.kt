package io.olkkani.lfr.entity.mongo

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "todayItemPrice")
@CompoundIndex(
    name = "compound_index_1",
    def = "{'itemCode': 1, 'endDate': 1}",
    unique = true
)
class TodayItemPrice (
    @Id
    val id: ObjectId? = null,
    val itemCode: Int,
    val endDate: LocalDateTime,
    val price: Int,
)