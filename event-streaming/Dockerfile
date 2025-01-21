FROM gradle:8.0-jdk17-alpine AS build
WORKDIR /app
COPY . .
WORKDIR /app/event-streaming
RUN gradle clean bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /app/event-streaming/build/libs/*.jar event-streaming.jar
ENTRYPOINT ["java", "-jar", "event-streaming.jar","--debug"]
