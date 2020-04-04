### STAGE 1: Build ###
FROM maven:3.6.3-jdk-11-openj9 AS build
COPY pom.xml /usr/local/server/pom.xml
COPY src /usr/local/server/src
WORKDIR /usr/local/server
RUN mvn clean package

### STAGE 2: Run ###
FROM tomcat:latest
ENV clinic_app_data_folder="/var/tmp/"
COPY --from=build /usr/local/server/target/Server-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/Server.war