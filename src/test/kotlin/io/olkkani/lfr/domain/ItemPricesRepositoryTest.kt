package io.olkkani.lfr.domain

import io.hypersistence.tsid.TSID
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemPricesRepositoryTest @Autowired constructor(
    private val repository: ItemPricesRepository
){

    @Test
    fun `아이템 저장 및 조회 테스트`() {
        // given
        val id = TSID.Factory.getTsid().toLong()
        val item = ItemPrices(
            id = id,
            itemCode = 65021100,
            closePrice = 11111,
            openPrice = 11000,
            highPrice = 12000,
            lowPrice = 10000,
            recordedDate = LocalDate.now()
        )
        val savedItem = repository.save(item)
        // when
        val findItem = repository.findById(savedItem.id!!).orElse(null)
        //then
        Assertions.assertThat(findItem.itemCode).isEqualTo(savedItem.itemCode)
        println(findItem.recordedDate.toString())
    }
}