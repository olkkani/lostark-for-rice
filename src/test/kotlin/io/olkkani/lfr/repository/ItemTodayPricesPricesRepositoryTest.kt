package io.olkkani.lfr.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.entity.ItemPrices
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@DataJpaTest
@ActiveProfiles("test")
class ItemTodayPricesPricesRepositoryTest @Autowired constructor(
    private val repository: ItemPricesRepository
){

    private val logger = KotlinLogging.logger {}
    @Test
    @DisplayName("아이템 저장 테스트")
    fun saveItemTest() {
//         given
        val item = ItemPrices(
            itemCode = 65021100,
            closePrice = 11111,
            openPrice = 11000,
            highPrice = 12000,
            lowPrice = 10000,
            recordedDate = LocalDate.now()
        )
        val savedItem = repository.save(item)
//         when
        val findItem = repository.findById(savedItem.id!!).orElse(null)
//        then
        Assertions.assertThat(findItem.itemCode).isEqualTo(savedItem.itemCode)
    }

    @Test
    @DisplayName("get item five rows test")
    fun getItemFiveRowsTest() {

    }
}