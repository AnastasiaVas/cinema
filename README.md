# Cinema

Cinema test task.
## Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)
- [PostgreSQL](https://www.postgresql.org)
- [Docker](https://www.docker.com/) for running integration tests

## Running the application locally

1. Provide environment variables for PostgreSQL user: spring.datasource.username and spring.datasource.password.

2. Execute the `main` method in the `com.test.cinema.CinemaApplication` class from your IDE.

   Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:
   
   ```shell
   mvn spring-boot:run
   ```