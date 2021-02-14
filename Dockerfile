FROM openjdk:8-alpine

LABEL maintainer="eduardo_gd@hotmail.com"

VOLUME /tmp

ADD target/ec2starter*.jar ec2starter.jar

WORKDIR /

ENTRYPOINT ["java","-jar", "/ec2starter.jar"]
