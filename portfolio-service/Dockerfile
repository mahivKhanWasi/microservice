FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/portfolio-service-0.0.1-SNAPSHOT.jar portfolio-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "portfolio-service.jar"]