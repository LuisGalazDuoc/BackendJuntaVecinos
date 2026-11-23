# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-21 AS compilacion
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2,sharing=locked mvn -B -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --no-create-home junta
COPY --from=compilacion /src/target/*.jar app.jar
USER junta
ENTRYPOINT ["java", "-jar", "app.jar"]
