FROM gradle:8.13.0-jdk17-alpine AS builder

WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
# 필요한 경우 gradlew 및 gradlew.bat 파일도 복사합니다.
COPY gradlew .
COPY gradlew.bat .

# 의존성 파일들을 복사하여 의존성 다운로드 단계의 캐시를 활용합니다.
COPY ./gradle/wrapper ./gradle/wrapper
# RUN gradle clean build --no-daemon --refresh-dependencies # 의존성만 미리 다운로드하는 단계 (선택 사항)

COPY src ./src

RUN gradle clean build --no-daemon -x test --stacktrace


FROM gradle:8.13.0-jdk17-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/MCPanda-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]