FROM openjdk:17

WORKDIR /app

COPY build/libs/payment-service-0.1.jar /app/payment-service.jar

COPY GeoLite2-City.mmdb /app/GeoLite2-City.mmdb

CMD ["java", "-jar", "payment-service.jar"]