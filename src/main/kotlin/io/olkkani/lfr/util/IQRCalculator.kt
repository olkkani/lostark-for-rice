package io.olkkani.lfr.util

class IQRCalculator(private val data: List<Int>) {
    private val filteredData: List<Double> = removeOutliers()

    // Q1, Q3 및 IQR을 계산하는 함수
    private fun calculateIQR(): Pair<Double, Double> {
        val sortedData = data.map{it.toDouble()}.sorted()
        val q1: Double = sortedData[(sortedData.size * 0.25).toInt()]
        val q3 = sortedData[(sortedData.size * 0.75).toInt()]
        return Pair(q1, q3)
    }

    // IQR을 사용해 이상치를 제거한 데이터를 반환하는 함수
    private fun removeOutliers(): List<Double> {
        val (q1, q3) = calculateIQR()
        val iqr = q3 - q1
        val lowerBound = q1 - 1.5 * iqr
        val upperBound = q3 + 1.5 * iqr
        return data.map { it.toDouble() }.filter { it in lowerBound..upperBound }
    }

    // 이상치를 제거한 데이터의 최저값을 반환
    fun getMin(): Int = filteredData.minOrNull()?.toInt() ?: 0

    // 이상치를 제거한 데이터의 최대값을 반환
    fun getMax(): Int = filteredData.maxOrNull()?.toInt() ?: 0
}