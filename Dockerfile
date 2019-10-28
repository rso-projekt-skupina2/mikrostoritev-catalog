FROM openjdk:8

RUN mkdir /app

WORKDIR /app

ADD ./api/target/image-catalog-api-1.0.0-SNAPSHOT.jar /app

EXPOSE 8080

CMD ["java", "-jar", "image-catalog-api-1.0.0-SNAPSHOT.jar"]