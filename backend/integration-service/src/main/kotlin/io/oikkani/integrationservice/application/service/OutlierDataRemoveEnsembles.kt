package io.oikkani.integrationservice.application.service

import org.springframework.stereotype.Service
import kotlin.math.abs

@Service
class OutlierDataRemoveEnsembles {

    /**
     * 데이터 개수에 따라 적절한 앙상블 방법으로 이상치를 제거합니다.
     * 
     * @param data 원본 데이터 리스트
     * @return 이상치가 제거된 데이터 리스트
     */
    fun removeOutlierData(data: List<Double>): List<Double> {
        if (data.isEmpty()) return emptyList()
        
        // 10개 이하일 경우 이상치 제거하지 않음
        if (data.size <= 10) {
            return data
        }
        
        return when (data.size) {
            in 11..30 -> removeOutliersWithLightEnsemble(data)
            in 31..100 -> removeOutliersWithMediumEnsemble(data)
            in 101..500 -> removeOutliersWithHeavyEnsemble(data)
            else -> removeOutliersWithFullEnsemble(data) // 500개 초과
        }
    }
    
    /**
     * 가벼운 앙상블 (11~30개): IQR만 사용
     */
    private fun removeOutliersWithLightEnsemble(data: List<Double>): List<Double> {
        val iqrOutliers = detectOutliersIQR(data)
        return data.filterIndexed { index, _ -> !iqrOutliers[index] }
    }
    
    /**
     * 중간 앙상블 (31~100개): IQR + Modified Z-Score
     */
    private fun removeOutliersWithMediumEnsemble(data: List<Double>): List<Double> {
        val iqrOutliers = detectOutliersIQR(data)
        val zScoreOutliers = detectOutliersModifiedZScore(data)
        
        // 두 방법 중 하나라도 이상치로 판단하면 제거
        val combinedOutliers = iqrOutliers.zip(zScoreOutliers) { iqr, z -> iqr || z }
        return data.filterIndexed { index, _ -> !combinedOutliers[index] }
    }
    
    /**
     * 무거운 앙상블 (101~500개): IQR + Modified Z-Score + Percentile
     */
    private fun removeOutliersWithHeavyEnsemble(data: List<Double>): List<Double> {
        val iqrOutliers = detectOutliersIQR(data)
        val zScoreOutliers = detectOutliersModifiedZScore(data)
        val percentileOutliers = detectOutliersPercentile(data)
        
        // 3개 방법 중 2개 이상이 이상치로 판단하면 제거 (다수결)
        val combinedOutliers = (0 until data.size).map { i ->
            val outlierCount = listOf(iqrOutliers[i], zScoreOutliers[i], percentileOutliers[i]).count { it }
            outlierCount >= 2
        }
        
        return data.filterIndexed { index, _ -> !combinedOutliers[index] }
    }
    
    /**
     * 전체 앙상블 (500개 초과): 모든 방법 사용, 가중치 적용
     */
    private fun removeOutliersWithFullEnsemble(data: List<Double>): List<Double> {
        val iqrOutliers = detectOutliersIQR(data)
        val zScoreOutliers = detectOutliersModifiedZScore(data)
        val percentileOutliers = detectOutliersPercentile(data)
        
        // 가중치 적용 (IQR: 0.4, Modified Z-Score: 0.4, Percentile: 0.2)
        val combinedOutliers = (0 until data.size).map { i ->
            val score = (if (iqrOutliers[i]) 0.4 else 0.0) +
                       (if (zScoreOutliers[i]) 0.4 else 0.0) +
                       (if (percentileOutliers[i]) 0.2 else 0.0)
            score >= 0.5 // 임계값 0.5 이상이면 이상치로 판단
        }
        
        return data.filterIndexed { index, _ -> !combinedOutliers[index] }
    }
    
    /**
     * IQR 방법으로 이상치 탐지
     */
    private fun detectOutliersIQR(data: List<Double>): List<Boolean> {
        val sortedData = data.sorted()
        val n = sortedData.size
        
        val q1Index = (n * 0.25).toInt()
        val q3Index = (n * 0.75).toInt()
        
        val q1 = sortedData[q1Index]
        val q3 = sortedData[q3Index]
        val iqr = q3 - q1
        
        val lowerBound = q1 - 1.5 * iqr
        val upperBound = q3 + 1.5 * iqr
        
        return data.map { it !in lowerBound..upperBound }
    }
    
    /**
     * Modified Z-Score 방법으로 이상치 탐지
     */
    private fun detectOutliersModifiedZScore(data: List<Double>, threshold: Double = 3.5): List<Boolean> {
        val median = calculateMedian(data)
        val deviations = data.map { abs(it - median) }
        val mad = calculateMedian(deviations) // Median Absolute Deviation
        
        return if (mad == 0.0) {
            // MAD가 0이면 모든 값이 같으므로 이상치 없음
            List(data.size) { false }
        } else {
            data.map { 
                val modifiedZScore = 0.6745 * abs(it - median) / mad
                modifiedZScore > threshold 
            }
        }
    }
    
    /**
     * Percentile 방법으로 이상치 탐지
     */
    private fun detectOutliersPercentile(data: List<Double>, lowerPercentile: Double = 0.05, upperPercentile: Double = 0.95): List<Boolean> {
        val sortedData = data.sorted()
        val n = sortedData.size
        
        val lowerIndex = ((n - 1) * lowerPercentile).toInt()
        val upperIndex = ((n - 1) * upperPercentile).toInt()
        
        val lowerBound = sortedData[lowerIndex]
        val upperBound = sortedData[upperIndex]
        
        return data.map { it < lowerBound || it > upperBound }
    }
    
    /**
     * 중앙값 계산
     */
    private fun calculateMedian(data: List<Double>): Double {
        val sortedData = data.sorted()
        val n = sortedData.size
        
        return if (n % 2 == 0) {
            (sortedData[n / 2 - 1] + sortedData[n / 2]) / 2.0
        } else {
            sortedData[n / 2]
        }
    }
    
    /**
     * 이상치 제거 통계 정보 반환
     */
    fun getOutlierRemovalStats(originalData: List<Double>): OutlierRemovalStats {
        val cleanedData = removeOutlierData(originalData)
        val removedCount = originalData.size - cleanedData.size
        val removalRate = if (originalData.isNotEmpty()) removedCount.toDouble() / originalData.size else 0.0
        
        return OutlierRemovalStats(
            originalSize = originalData.size,
            cleanedSize = cleanedData.size,
            removedCount = removedCount,
            removalRate = removalRate,
            method = getEnsembleMethodByDataSize(originalData.size)
        )
    }
    
    private fun getEnsembleMethodByDataSize(size: Int): String {
        return when (size) {
            in 0..10 -> "No removal (too small dataset)"
            in 11..30 -> "Light ensemble (IQR only)"
            in 31..100 -> "Medium ensemble (IQR + Modified Z-Score)"
            in 101..500 -> "Heavy ensemble (IQR + Modified Z-Score + Percentile, majority vote)"
            else -> "Full ensemble (All methods with weighted scoring)"
        }
    }
}

/**
 * 이상치 제거 통계 정보를 담는 데이터 클래스
 */
data class OutlierRemovalStats(
    val originalSize: Int,
    val cleanedSize: Int,
    val removedCount: Int,
    val removalRate: Double,
    val method: String
)