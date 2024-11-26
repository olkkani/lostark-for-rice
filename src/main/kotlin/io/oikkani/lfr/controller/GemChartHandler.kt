package io.oikkani.lfr.controller

import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono


@Controller
class GemChartHandler {

    fun gemChart(): Mono<ServerResponse> {
        return ServerResponse.ok().bodyValue("hello")
    }

}