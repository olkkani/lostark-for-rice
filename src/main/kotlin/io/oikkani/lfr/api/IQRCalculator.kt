package io.oikkani.lfr.api

class IQRCalculator(private val data: List<Double>) {
    private val filteredData: List<Double> = removeOutliers()

    // Q1, Q3 및 IQR을 계산하는 함수
    private fun calculateIQR(): Pair<Double, Double> {
        val sortedData = data.sorted()
        val q1 = sortedData[(sortedData.size * 0.25).toInt()]
        val q3 = sortedData[(sortedData.size * 0.75).toInt()]
        return Pair(q1, q3)
    }

    // IQR을 사용해 이상치를 제거한 데이터를 반환하는 함수
    private fun removeOutliers(): List<Double> {
        val (q1, q3) = calculateIQR()
        val iqr = q3 - q1
        val lowerBound = q1 - 1.5 * iqr
        val upperBound = q3 + 1.5 * iqr
        return data.filter { it in lowerBound..upperBound }
    }

    // 이상치를 제거한 데이터의 최저값을 반환
    fun getMin(): Double? = filteredData.minOrNull()

    // 이상치를 제거한 데이터의 최대값을 반환
    fun getMax(): Double? = filteredData.maxOrNull()

    // 이상치를 제거한 데이터의 평균값을 반환
    fun getAverage(): Double = if (filteredData.isNotEmpty()) filteredData.average() else Double.NaN
}

fun main() {
    val data = listOf(10.0, 12.0, 14.0, 15.0, 16.0, 18.0, 19.0, 21.0, 50.0, 100.0)
    val iqrCalculator = IQRCalculator(data)

    println("Filtered Data Statistics:")
    println("Min: ${iqrCalculator.getMin()}")
    println("Max: ${iqrCalculator.getMax()}")
    println("Average: ${iqrCalculator.getAverage()}")
}