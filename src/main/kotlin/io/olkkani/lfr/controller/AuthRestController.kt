package io.olkkani.lfr.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/auth")
class AuthRestController(
) {
//    @PostMapping("/refresh")
//    fun refreshToken(
//            @CookieValue("refresh_token") refreshToken: String,
//            @RequestHeader("device-id", required = false) deviceId: String? = null,
//            ): ResponseEntity<Map<String, String>> {
//        val userId = jwtTokenProvider.getUserNameByToken(refreshToken)
//        val newAccessToken = jwtTokenProvider.createToken(userId)
//        return ResponseEntity.ok(mapOf("accessToken" to newAccessToken))
//    }
//
}