FROM gradle:8.0-jdk17-alpine AS build
WORKDIR /app
COPY . .
WORKDIR /app/flight-data-loader
RUN gradle clean bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /app/flight-data-loader/build/libs/*.jar data-loader.jar
ENTRYPOINT ["java", "-jar", "data-loader.jar","--debug"]
