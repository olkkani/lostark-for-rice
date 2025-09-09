package io.oikkani.integrationservice.infrastructure.adapter.inbound.exception

class CustomException(val exceptionCode: ExceptionCode) : RuntimeException()