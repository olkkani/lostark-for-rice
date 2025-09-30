# 실행 스테이지
FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY build/libs/processor-service.jar /app/processor-service.jar
EXPOSE 8080
CMD ["java", "-jar", "processor-service.jar"]