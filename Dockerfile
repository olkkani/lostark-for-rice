# 실행 스테이지
FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY build/libs/lostark-for-rice.jar /app/lostark-for-rice.jar
EXPOSE 8080
CMD ["java", "-jar", "lostark-for-rice.jar"]