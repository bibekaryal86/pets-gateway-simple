# pets-gateway-simple

* Main Repo: https://github.com/bibekaryal86/pets-gateway

This is a simple app which provides gateway routing functions for Personal Expenses Tracking System application. This
app is a scaled down version of `pets-gateway` app found here: https://github.com/bibekaryal86/pets-gateway. The other
app uses Spring Cloud Gateway framework to do the exact same function as this app

- `pets-gateway-simple`. However, this `simple` app does not use any kind of Spring or any other frameworks. The web
  application framework is provided by Jetty server with Java Servlets providing the endpoints. Interactions to other
  REST services are done by Java native HttpClient.

Because of absence of any frameworks, the footprint of this app is very grounded (~6 MB jar archive and ~100 MB runtime
JVM memory) as opposed to when using Spring Boot (~45 MB archive and ~350 MB memory). And, as a result, the app can be
deployed and continuously run 24/7 on Google Cloud Platform App Engine's free tier.

To run the app, we need to supply the following environment variables:

* Port
    * PORT: This is optional, and if it is not provided port defaults to 8080
* Active Profile
    * SPRING_PROFILES_ACTIVE (development, docker, production)
* PETS Service Security Details:
    * BASIC_AUTH_USR_PETSSERVICE (auth username of pets-service)
    * BASIC_AUTH_PWD_PETSSERVICE (auth password of pets-service)
* PETS Database Security Details:
    * BASIC_AUTH_USR_PETSDATABASE (auth username of pets-database)
    * BASIC_AUTH_PWD_PETSDATABASE (auth password of pets-database)
* JWT Signing Key
    * SECRET_KEY: to sign JWT tokens (not needed in `pets-gateway`)

The app has been deployed to GCP:

* https://pets-gateway.appspot.com/tests/ping
