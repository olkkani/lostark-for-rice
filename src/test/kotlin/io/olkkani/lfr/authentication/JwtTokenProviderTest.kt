package io.olkkani.lfr.authentication

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.olkkani.lfr.util.JwtTokenProvider
import java.util.*
import javax.crypto.SecretKey

class JwtTokenProviderTest : DescribeSpec({

    describe("JwtTokenProvider 테스트") {
        // 테스트를 위한 설정값
        val secret = "thisIsASecretKeyForTestingThatShouldBeLongEnoughForHS256Algorithm"
        val validityInMilliseconds = 3600000L // 1시간

        // JwtTokenProvider 인스턴스 생성
        val jwtTokenProvider = JwtTokenProvider(secret, validityInMilliseconds)

        // SecretKey 초기화 (실제 구현과 같은 방식)
        val secretKey: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret))

        context("토큰 생성 테스트") {
            val userName = "testUser"

            it("유효한 토큰이 생성되어야 한다") {
                val token = jwtTokenProvider.createToken(userName)

                // 토큰은 비어있지 않아야 함
                token.shouldNotBeEmpty()
            }

            it("생성된 토큰에서 userName을 추출할 수 있어야 한다") {
                val token = jwtTokenProvider.createToken(userName)
                val extractedUserName = jwtTokenProvider.getUserNameByToken(token)

                extractedUserName shouldBe userName
            }
        }

        context("토큰 검증 테스트") {
            val userName = "testUser"

            it("유효한 토큰은 검증을 통과해야 한다") {
                val token = jwtTokenProvider.createToken(userName)
                val isValid = jwtTokenProvider.validateToken(token)

                isValid shouldBe true
            }

            it("만료된 토큰은 검증에 실패해야 한다") {
                // 만료된 토큰을 생성하기 위해 커스텀 메서드 작성
                val expiredToken = createExpiredToken(secretKey, userName)
                val isValid = jwtTokenProvider.validateToken(expiredToken)

                isValid shouldBe false
            }

            it("변조된 토큰은 검증에 실패해야 한다") {
                val token = jwtTokenProvider.createToken(userName)
                val tamperedToken = token + "tampered"
                val isValid = jwtTokenProvider.validateToken(tamperedToken)

                isValid shouldBe false
            }
        }
    }
})

/**
 * 테스트용 만료된 토큰 생성 메서드
 */
private fun createExpiredToken(secretKey: SecretKey, userName: String): String {
    val now = Date()
    val expiredDate = Date(now.time - 1000) // 1초 전에 만료됨

    return Jwts.builder()
        .audience().add(userName).and()
        .issuedAt(now)
        .expiration(expiredDate) // 만료된 시간 설정
        .signWith(secretKey)
        .compact()
}