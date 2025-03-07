# 빌드 스테이지
FROM gradle:8.13-jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# 실행 스테이지
FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY --from=build /app/build/libs/lostark-for-rice.jar /app/lostark-for-rice.jar
EXPOSE 8080
CMD ["java", "-jar", "lostark-for-rice.jar"]