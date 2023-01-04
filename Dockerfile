FROM openjdk:11

ARG PROFILE=local

WORKDIR app
ADD target/rental-service-1.0-SNAPSHOT.jar /app/rental-service-1.0-SNAPSHOT.jar

COPY target/classes/application.properties /app/application.properties
COPY target/classes/application-${PROFILE}.properties /app/application-${PROFILE}.properties

EXPOSE 8080

CMD java -jar rental-service-1.0-SNAPSHOT.jar