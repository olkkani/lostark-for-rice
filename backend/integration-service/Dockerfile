# 실행 스테이지
FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY build/libs/integration-service.jar /app/integration-service.jar
EXPOSE 8080
CMD ["java", "-jar", "integration-service.jar"]