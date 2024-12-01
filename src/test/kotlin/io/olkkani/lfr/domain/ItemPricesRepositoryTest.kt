package io.olkkani.lfr.domain

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

//@DataJpaTest
//@ActiveProfiles("test")
//class ItemPricesRepositoryTest @Autowired constructor(
//    private val repository: ItemPricesRepository
//){
//
//    @Test
//    fun `아이템 저장 및 조회 테스트`() {
//         given
//        val item = ItemPrices(
//            itemCode = 65021100,
//            closePrice = 11111,
//            openPrice = 11000,
//            highPrice = 12000,
//            lowPrice = 10000,
//            recordedDate = LocalDate.now()
//        )
//        val savedItem = repository.save(item)
//         when
//        val findItem = repository.findById(savedItem.id!!).orElse(null)
//        then
//        Assertions.assertThat(findItem.itemCode).isEqualTo(savedItem.itemCode)
//    }
//}