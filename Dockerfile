FROM maven:3.9.6-amazoncorretto-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

FROM builder as pkg
COPY src ./src
RUN mvn package -Dmaven.test.skip

# Second stage: run the jar using Corretto
FROM amazoncorretto:24-al2023-headful
COPY --from=pkg /app/target/*.jar app.jar
RUN mkdir /mnt/ftp
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]