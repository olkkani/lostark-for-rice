package io.olkkani.lfr

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.api.LostarkAPIClient
import io.olkkani.lfr.model.AuctionRequest

fun main() {
    val logger = KotlinLogging.logger {}
    val apiKey: String =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyIsImtpZCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyJ9.eyJpc3MiOiJodHRwczovL2x1ZHkuZ2FtZS5vbnN0b3ZlLmNvbSIsImF1ZCI6Imh0dHBzOi8vbHVkeS5nYW1lLm9uc3RvdmUuY29tL3Jlc291cmNlcyIsImNsaWVudF9pZCI6IjEwMDAwMDAwMDA1Njg0NDAifQ.p2x1w9cwXtejBvX5n2Ziay8O6_4ji35GnQsILEZ6ivdOqqolvzfLMtyuqkENTd50dFv6SnDx6H9QJP4kxaiGcjEnIcbVZPAyH9tjexlXVj19kiEoXrD9rAQYs7K82cw_7WkpAHwvVs-gMfdXHVusVA4nm148f_3BVsOLcTnqwDsKgaNBk5xrpb8Zjftotyrm-N0gv3-wYKr_CI-74jlHXx5DvfR3ygRNHWSoINKw49W6wOFSU_IMcSpRTRJd4O4ul92oLBmehu4COEltvp78qcaPW1HOWzwQCfxsoRutEhdWfoLGeTGsAji0pvQQLrbCoYIDruIW6Xowl_zRMtz9og"
    val apiClient = LostarkAPIClient(apiKey)

    // given
    val request = AuctionRequest(itemName = "10레벨 멸화의 보석", itemTier = 3, itemGrade = "유물", itemCode = 65021100)

//    apiClient.getAuctionItems(request).subscribe({ response ->
//        response.items.forEach { item ->
//            logger.error { item.auctionInfo.buyPrice }
//        }
//    })
}
