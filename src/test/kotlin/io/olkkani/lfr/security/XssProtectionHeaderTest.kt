package io.olkkani.lfr.security

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.Ignore

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Ignore
class XssProtectionHeaderTest(
    @Autowired
    private var mockMvc: MockMvc
): DescribeSpec() {

    init {
        this.describe("X-XSS-Protection header is set to 1 mode=block"){
            context("X-XSS-Protection header") {
                it("should be set to '1; mode=block'") {
                    val result = mockMvc.get("/")  // 테스트할 엔드포인트 (필요 시 수정)
                        .andReturn()

                    val headerValue = result.response.getHeader("X-XSS-Protection")
                    headerValue shouldBe "1; mode=block"
                }
            }
        }
    }
}