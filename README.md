# Cinema

Cinema test task.
## Requirements

For building and running the application you need:

- [JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
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