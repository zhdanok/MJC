# MJC-3-multimodule-spring-boot-hibernate-jpa-hateoas-pagination

There is a multimodule application which include following modules:

- DAO;
- SERVICE;
- WEB - default port 8091;

## Local setup

### To install docker, follow instructions specific to your OS: https://docs.docker.com/engine/install/

This application using MySQL8 and Docker-compose

### Run docker-compose (sudo) docker-compose up

### If You use IDE:

- run WebApplication (classpath: web/src/main/java/com/epam/esm/WebApplication) and go to http://localhost:8091/gifts

### If You use command line:

- build App (mvn clean install)
- run App (java -jar web/target/web-1.0.3-SNAPSHOT-spring-boot.jar) and go to http://localhost:8091/gifts
- stop App (lsof -i ---> kill PID)

### Demonstration

- Open you browser http://localhost:8091/gifts or http://localhost:8091/gifts/{id}
- Try to open http://localhost:8091/tags/** or http://localhost:8091/users/**. App redirect you to auth form.
- You can register (only user role) or use existing users (misha/123/id=1301 as User, donte1234/147258/id=1 as Admin)
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
