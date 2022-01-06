FROM openjdk:11-jre-slim-bullseye
RUN adduser --system --group springdocker
USER springdocker:springdocker
ARG JAR_FILE=app/build/libs/pets-gateway-simple.jar
COPY ${JAR_FILE} pets-gateway.jar
ENTRYPOINT ["java","-jar", \
"/pets-gateway.jar"]
# Environment variables to be prdvided in docker-compose
