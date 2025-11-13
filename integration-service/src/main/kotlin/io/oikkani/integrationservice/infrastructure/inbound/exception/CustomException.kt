package io.oikkani.integrationservice.infrastructure.inbound.exception

class CustomException(val exceptionCode: ExceptionCode) : RuntimeException()