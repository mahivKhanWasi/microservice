FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/auth-service-0.0.1-SNAPSHOT.jar auth-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "auth-service.jar"]