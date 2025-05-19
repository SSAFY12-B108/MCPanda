FROM gradle:7.5-jdk17 AS builder

WORKDIR /app

COPY --chown=gradle:gradle build.gradle .
COPY --chown=gradle:gradle settings.gradle .
COPY --chown=gradle:gradle gradlew .
COPY --chown=gradle:gradle gradlew.bat .

COPY --chown=gradle:gradle src ./src

USER gradle
RUN chmod +x gradlew
RUN ./gradlew clean build --no-daemon -x test

FROM openjdk:17-jre-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]