# MJC-3-multimodule-spring-boot-hibernate-jpa-hateoas-pagination

There is a multimodule application which include following modules:

- DAO;
- SERVICE;
- WEB - default port 8091;

## Local setup

This application using MySQL8.

- For creating necessary database and tables run DaoApplication (classpath:
  dao/src/main/java/com/epam/esm/DaoApplication)
- For generating data into tables You can change propertie populate.database to "true" in test.properties and then run
  Tests loadDataToTables() (classpath:
  service/src/test/java/com/epam/esm/service)

### If You use IDE:

- run WebApplication (classpath: web/src/main/java/com/epam/esm/WebApplication) and go to http://localhost:8091/gifts

### If You use command line:

- build App (mvn clean install)
- run App (java -jar web/target/web-1.0.3-SNAPSHOT-spring-boot.jar) and go to http://localhost:8091/gifts
- stop App (lsof -i ---> kill PID)