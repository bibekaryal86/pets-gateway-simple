FROM openjdk:11-jre-slim-bullseye
RUN adduser --system --group springdocker
USER springdocker:springdocker
ARG JAR_FILE=app/build/libs/pets-gateway-simple.jar
COPY ${JAR_FILE} pets-gateway.jar
ENTRYPOINT ["java","-jar", \
#"-DSPRING_PROFILES_ACTIVE=docker", \
#"-DTZ=America/Denver", \
#"-DBASIC_AUTH_USR_PETSSERVICE=some_username", \
#"-DBASIC_AUTH_PWD_PETSSERVICE=some_password", \
#"-DBASIC_AUTH_USR_PETSDATABASE=some_username", \
#"-DBASIC_AUTH_PWD_PETSDATABASE=some_password", \
#"-DSECRET_KEY=some_secret_key", \
"/pets-gateway.jar"]
# Environment variables to be prdvided in docker-compose
