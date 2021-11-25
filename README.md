## MJC-4-multimodule-Spring-Security-OAuth2.0-Spring-Data-JPA

- RESOURCE-SERVER (App) - port 8091;
- AUTH-SERVER (KeyCloak) - port 9000;
- CLIENT (Postman);

### Local setup

- This application using MySQL8 and Docker-compose
- To install docker, follow instructions specific to your OS: https://docs.docker.com/engine/install/
- Run docker-compose by writing follow command in terminal: (sudo) docker-compose up
- Run Postman;

### If You use IDE:

- run WebApplication (classpath: web/src/main/java/com/epam/esm/WebApplication)

### If You use command line:

- build App (mvn clean install)
- run App (java -jar web/target/web-1.0.3-SNAPSHOT-spring-boot.jar)
- stop App (lsof -i ---> kill PID)

### Demonstration

- Use Postman for generating JWT (You can get all properties in web/src/main/resources/application.properties)
- App support follow Flows: Implicit, Resource Owner Credentials, Authorization code
- Permissions:
- Guest:
  * Read operations for main entity. (http://localhost:8091/gifts or http://localhost:8091/gifts/{id})
  * Signup.
  * Login.
- User:
  * Make an order on main entity.
  * All read operations.(http://localhost:8091/users/{id} - only for self id; http://localhost:8091/tags/**)
- Administrator (can be added only via database call):
  * All operations, including addition and modification of entities(http://localhost:8091/users/**
    ; http://localhost:8091/tags/**).
