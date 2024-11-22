package io.oikkani.lfr

import io.oikkani.lfr.domain.ItemPricesRepository
import io.oikkani.lfr.service.AuctionService
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
class LostarkForRiceApplicationTests {

	@Mock
	private lateinit var repository: ItemPricesRepository

	@InjectMocks
	private lateinit var service: AuctionService

//	@Test
//	fun

}
