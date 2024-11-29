package io.olkkani.lfr.util

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.api.LostarkAPIClient
import org.junit.jupiter.api.Test

class LostarkAPITest (
){
    var apiKey = ""
    val apiClient = LostarkAPIClient(apiKey)
    private val logger = KotlinLogging.logger {}

    @Test
    fun `API_가져오기_TEST` (){
        // given
    }
}