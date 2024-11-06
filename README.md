# Navigation Log Service
__Description__

This application provides the REST API end-points to create, read and delete navigation log data.<br>
<p>
The application has been built using:
Kotlin 1.9.22<br>
Java 21<br>
Springboot 3.3.4<br>
PostgreSql<br>
Liquibase<br>
</p>

<hr>

__Build__
<br>To build the executable Jar, run the following gradle command in the project root directory:<br>
``$ ./gradlew clean build``

It will create a jar navlog-service-0.0.1-SNAPSHOT.jar in <project root>/build/libs/ directory.<br>

__Run__

__Local run__

Make sure that PostgreSql Server is up and running. Also make sure that the database navlog_db is created on the DB server.
For example, if you are using docker:<br>
``$ docker run --name postgres -p 5432:5432 -e POSTGRES_USER=root -e POSTGRES_PASSWORD=secret -d postgres:16``
``$ docker exec -it postgres createdb --username=root --owner=root navlog_db``

<br>To run the program:<br>

``$ ./gradlew bootRun --args='--spring.profiles.active=local'``
<br>

__Docker run__

<strong>Note:</strong> The tomcat port has been set to 8080. If you want to change that, edit the api port in docker-compose file.<br>
> To build containers and run them<br>
``$ docker compose up --build``

> To just run the already build containers<br>
``$ docker compose up``

> To stop the containers<br>
``$ docker compose down``