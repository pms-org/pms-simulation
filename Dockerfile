
FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests || (sleep 5 && mvn package -DskipTests) || (sleep 10 && mvn package -DskipTests)

FROM eclipse-temurin:21

WORKDIR /app

RUN mkdir -p /app/logs

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]