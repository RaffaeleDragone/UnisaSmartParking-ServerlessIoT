
FROM maven:3.5-jdk-8-alpine
WORKDIR /app
COPY / /app
RUN mvn package

FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=0 /app/target/UnisaSmartParkingRest/UnisaSmartParkingRest.jar /app
RUN mkdir lib

COPY --from=0 /app/target/UnisaSmartParkingRest/lib /app/lib
COPY --from=0 /app/area1.json /app
COPY --from=0 /app/area2.json /app
COPY --from=0 /app/myLog /app

CMD /usr/bin/java -jar UnisaSmartParkingRest.jar
