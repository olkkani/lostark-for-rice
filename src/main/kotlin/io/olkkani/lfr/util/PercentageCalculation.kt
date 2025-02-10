package io.olkkani.lfr.util

class PercentageCalculation {
    fun calc(newValue: Int, originalValue: Int): Double {
        require(originalValue != 0) { "originalValue must not be zero" }
        return ((newValue - originalValue).toDouble() / originalValue) * 100
    }
}