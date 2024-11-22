package io.oikkani.lfr.domain

import io.oikkani.lfr.util.createTsid
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate


@Table
data class ItemPricesTemp(
    @Id
    val id: Long = createTsid(),
    @Column
    val recordedDate: LocalDate,
    @Column
    val itemCode: Int,
    @Column
    val price: Int
)