package io.oikkani.lfr.service

import io.kotest.assertions.any
import io.kotest.core.spec.style.BehaviorSpec
import io.oikkani.lfr.domain.ItemPrices
import io.oikkani.lfr.domain.ItemPricesRepository
import io.oikkani.lfr.domain.ItemPricesTemp
import io.oikkani.lfr.domain.ItemPricesTempRepository
//import io.oikkani.lfr.model.GemInfo
import io.oikkani.lfr.model.gemsInfo
import io.oikkani.lfr.api.LostarkAPIClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Mono
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class AuctionServiceTest: BehaviorSpec(){

    @Mock
    private lateinit var repository: ItemPricesRepository

    @Mock
    private lateinit var tempRepository: ItemPricesTempRepository

    @Mock
    private lateinit var apiClient: LostarkAPIClient

    @InjectMocks
    private lateinit var service: AuctionService

//    private lateinit var gemInfo: List<Pair<String, GemInfo>>
//    private lateinit var today: LocalDate

}