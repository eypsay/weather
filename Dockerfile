FROM openjdk:17-jdk-slim AS build

COPY ./weather/pom.xml ./weather/mvnw ./
COPY ./weather/.mvn .mvn
RUN ./mvnw dependency:resolve

COPY ./weather/src src
RUN ./mvnw package

FROM openjdk:17-jdk-slim
WORKDIR weather
COPY --from=build target/*.jar weather.jar
ENTRYPOINT ["java", "-jar", "weather.jar"]