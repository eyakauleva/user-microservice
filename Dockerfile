FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /
COPY src /src
COPY pom.xml /
RUN mvn -f /pom.xml clean package -Dmaven.test.skip

FROM openjdk:17-jdk-slim
WORKDIR /
COPY --from=build /target/*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]