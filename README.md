
# CRUD Movies-Authors Application

This is a project which is created for maintaining sets of movies and authors, their properties and dependencies. It's implemented as a REST-service, which allows users to **CREATE**, **READ**, **UPDATE** and **DELETE** entities, which represent "Movie" and "Author".


## Requirements

To successfully run this application you should have:

- [Docker(Docker Engine)](https://www.docker.com/products/docker-desktop/) installed on your machine
- [JDK 16+](https://www.oracle.com/java/technologies/javase/jdk16-archive-downloads.html)
- [Maven 3.6+](https://maven.apache.org/docs/3.6.0/release-notes.html)


## Quick start

To start the application you should do the following:

 - Open the terminal/bash in the project root folder and run

```
mvn clean package
```
- After all tests are run and the jar-file is builded successfully run
```
 docker build -t jar -f docker/Dockerfile .
```
- After jar-file is added to container, type the following 
```
 cd docker-compose
```
```
 docker-compose up
```
After container is created and deployed, navigate to browser/Postman and start making requests on ***http://localhost:8080/*** (see [Usage](#Usage))
