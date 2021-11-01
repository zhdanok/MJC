# MJC-3-multimodule-spring-boot-hibernate-jpa-hateoas-pagination

There is a multimodule application which include following modules:

- DAO - default port 8092;
- SERVICE - default port 8093;
- WEB - default port 8091;

## Local setup

This application using MySQL8.

- For creating necessary database and tables run DaoApplication (classpath:
  dao/src/main/java/com/epam/esm/DaoApplication)
- For generating data into tables You can uncomment and pun Tests loadDataToTables() (classpath:
  service/src/test/java/com/epam/esm/service)

### NOTE: Please comment it after running again

- For running App run WebApplication (classpath: web/src/main/java/com/epam/esm/WebApplication) and go
  to http://localhost:8091/gifts