FROM gradle:8.0-jdk17-alpine AS build
WORKDIR /app
COPY . .
WORKDIR /app/api
RUN gradle clean bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /app/api/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
