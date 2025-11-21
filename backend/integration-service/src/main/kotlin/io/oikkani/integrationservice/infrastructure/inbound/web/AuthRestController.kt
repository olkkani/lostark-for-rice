package io.oikkani.integrationservice.infrastructure.inbound.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthRestController(
) {
    @GetMapping("/discord")
    fun loginWithDiscord(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(
            mapOf("redirectUrl" to "/oauth2/authorization/discord")
        )
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<Map<String, String>> {
        //TODO: 로그아웃 로직 구현
        return ResponseEntity.ok(mapOf("message" to "Successfully logged out"))
    }
}