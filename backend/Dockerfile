FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY pom.xml pom.xml
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

COPY src/ src/
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN useradd --system --create-home --shell /usr/sbin/nologin spring \
    && mkdir -p /app/logs \
    && chown -R spring:spring /app

COPY --from=build /app/target/*.jar /app/app.jar

ENV SERVER_PORT=8080
EXPOSE 8080

USER spring
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
