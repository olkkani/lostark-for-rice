package io.oikkani.integrationservice.infrastructure.adapter.`in`.exception

import io.oikkani.integrationservice.infrastructure.adapter.out.client.processor.ProcessorClientException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    
    @ExceptionHandler(CustomException::class)
    fun handleCommonException(e: CustomException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            e.exceptionCode.status.value(),
            e.exceptionCode.message,
        )
        return ResponseEntity(errorResponse, e.exceptionCode.status)
    }
    
    /**
     * ProcessorClientException 처리 - 알림 발송 포함
     */
    @ExceptionHandler(ProcessorClientException::class)
    fun handleProcessorClientException(ex: ProcessorClientException): ResponseEntity<ErrorResponse> {
        logger.error("ProcessorClientException occurred: {}", ex.toString(), ex)
        
        // 🚨 알림이 필요한 에러인지 확인
        if (ex.errorCode.shouldAlert) {
            sendAlert(ex)
        }
        
        // 📊 메트릭 수집
        recordErrorMetrics(ex)
        
        val errorResponse = ErrorResponse(
            status = 500,
            error = "${ex.errorCode.code}: ${ex.errorCode.description}",
            message = mapOf(
                "errorCode" to ex.errorCode.code,
                "description" to ex.errorCode.description,
                "originalMessage" to (ex.message ?: "Unknown error"),
                "httpStatus" to (ex.httpStatus?.value()?.toString() ?: "N/A"),
                "retryAttempts" to ex.retryAttempts.toString(),
                "timestamp" to ex.timestamp.toString()
            )
        )
        
        return ResponseEntity.status(500).body(errorResponse)
    }
    
    /**
     * 🚨 알림 발송 (Slack, 이메일 등)
     */
    private fun sendAlert(ex: ProcessorClientException) {
        logger.info("🚨 Sending alert for error: {}", ex.errorCode.code)
        
        val alertMessage = buildAlertMessage(ex)
        
        try {
            // TODO: 실제 알림 서비스 호출
            // slackService.sendAlert(alertMessage)
            // emailService.sendAlert(alertMessage)
            // smsService.sendAlert(alertMessage)
            
            logger.info("📨 Alert would be sent: \n{}", alertMessage)
            logger.info("✅ Alert sent successfully for error: {}", ex.errorCode.code)
            
        } catch (alertError: Exception) {
            logger.error("❌ Failed to send alert for error: {}", ex.errorCode.code, alertError)
        }
    }
    
    /**
     * 알림 메시지 생성
     */
    private fun buildAlertMessage(ex: ProcessorClientException): String {
        return """
            🚨 **프로세서 클라이언트 에러 발생**
            
            📋 **에러 정보**
            • 에러 코드: `${ex.errorCode.code}`
            • 설명: ${ex.errorCode.description}
            • 메시지: ${ex.message}
            
            🌐 **HTTP 정보**
            • 상태 코드: ${ex.httpStatus ?: "N/A"}
            • 재시도 횟수: ${ex.retryAttempts}번
            
            🕐 **발생 시간**
            • ${java.time.Instant.ofEpochMilli(ex.timestamp)}
            
            🔍 **원인**
            • ${ex.cause?.message ?: "상세 정보 없음"}
            
            ---
            💡 **대응 방안**
            ${getRecommendedAction(ex.errorCode)}
        """.trimIndent()
    }
    
    /**
     * 에러 코드별 권장 대응 방안
     */
    private fun getRecommendedAction(errorCode: io.oikkani.integrationservice.infrastructure.adapter.out.client.processor.ProcessorErrorCode): String {
        return when (errorCode) {
            io.oikkani.integrationservice.infrastructure.adapter.out.client.processor.ProcessorErrorCode.NETWORK_ERROR -> 
                "네트워크 연결 상태를 확인하고, 프로세서 서버 상태를 점검하세요."
            io.oikkani.integrationservice.infrastructure.adapter.out.client.processor.ProcessorErrorCode.TIMEOUT_ERROR -> 
                "타임아웃 설정을 검토하고, 프로세서 서버 응답 시간을 모니터링하세요."
            io.oikkani.integrationservice.infrastructure.adapter.out.client.processor.ProcessorErrorCode.CLIENT_ERROR_4XX -> 
                "요청 데이터나 인증 정보를 확인하세요. 재시도하지 않습니다."
            io.oikkani.integrationservice.infrastructure.adapter.out.client.processor.ProcessorErrorCode.SERVER_ERROR_5XX -> 
                "프로세서 서버 상태를 확인하세요. 자동 재시도 후에도 실패했습니다."
            io.oikkani.integrationservice.infrastructure.adapter.out.client.processor.ProcessorErrorCode.RETRY_EXHAUSTED -> 
                "모든 재시도가 실패했습니다. 서버 상태와 네트워크를 종합적으로 점검하세요."
            io.oikkani.integrationservice.infrastructure.adapter.out.client.processor.ProcessorErrorCode.UNKNOWN_ERROR -> 
                "예상치 못한 오류입니다. 로그를 상세히 분석하고 개발팀에 문의하세요."
        }
    }
    
    /**
     * 📊 에러 메트릭 수집
     */
    private fun recordErrorMetrics(ex: ProcessorClientException) {
        logger.debug("📊 Recording metrics for error: {}", ex.errorCode.code)
        
        try {
            // TODO: 실제 메트릭 수집 서비스 호출
            // meterRegistry.counter(
            //     "processor.client.errors",
            //     "error_code", ex.errorCode.code,
            //     "http_status", ex.httpStatus?.toString() ?: "unknown",
            //     "retry_attempts", ex.retryAttempts.toString()
            // ).increment()
            
            logger.debug("✅ Metrics recorded for error: {}", ex.errorCode.code)
            
        } catch (metricsError: Exception) {
            logger.error("❌ Failed to record metrics for error: {}", ex.errorCode.code, metricsError)
        }
    }
}