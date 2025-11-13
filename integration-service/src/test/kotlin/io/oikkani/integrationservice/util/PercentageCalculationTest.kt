//package io.oikkani.integrationservice.util
//
//import io.kotest.core.spec.style.DescribeSpec
//import io.kotest.matchers.shouldBe
//import io.olkkani.lfr.common.util.PercentageCalculation
//
//class rePercentageCalculationTest: DescribeSpec() {
//    init {
//        describe("Percentage calculation") {
//            context("110가 비교값, 100이 비교하고 싶은 비교값으로 지정하면"){
//                val newValue = 110
//                val originalValue = 100
//                val percentage = PercentageCalculation().calc(newValue, originalValue)
//                it("백분율 값은 10(%) 이다.") {
//                    percentage shouldBe 10.0
//                }
//            }
//        }
//    }
//}