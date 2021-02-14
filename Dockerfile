FROM openjdk:8-alpine

LABEL maintainer="eduardo_gd@hotmail.com"

VOLUME /tmp

ADD target/ec2starter*.jar ec2starter.jar

WORKDIR /

ENTRYPOINT ["java","-jar", "-DAWS_ACCESS_KEY_ID=${aws_access_key_id}", "-DAWS_SECRET_ACCESS_KEY=${aws_secret_access_key}", "/ec2starter.jar"]
