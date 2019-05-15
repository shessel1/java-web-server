FROM maven:3.6-jdk-11 AS build
WORKDIR /usr/src/app
COPY src/ src
COPY pom.xml .
RUN mvn clean test && mvn clean package

FROM openjdk:11
WORKDIR /usr/app
COPY --from=build /usr/src/app/target/webserver-*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","/usr/app/app.jar"]
