### STAGE 1: Build ###
FROM maven AS build
RUN apt-get update
RUN apt-get install -y maven
COPY pom.xml /usr/local/server/pom.xml
COPY src /usr/local/server/src
WORKDIR /usr/local/server
RUN mvm package

### STAGE 2: Run ###
FROM tomcat:latest
COPY --from=build /usr/local/server/target/Server-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/Server.war