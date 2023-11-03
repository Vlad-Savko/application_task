
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
## Usage

### Movies
#### Get all movies

```http
  GET http://localhost:8080/movies
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `-`       | `-`      | Returns all movies (JSON)  |

##### Response JSON-body example:
```http
{
    "Year of release": 2007,
    "Authors": [
        {
            "Id": 1,
            "firstName": "James",
            "lastName": "Cameron",
            "age": 44
        }
    ],
    "Rental finish date": "2003-09-04",
    "Id": 3,
    "Rental start date": "2003-12-02",
    "Name": "Avatar"
}
```

**HTTP Response Code:** 200 (OK)

#### Get one movie

```http
  GET http://localhost:8080/movies/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `long`   | **Required**. Id of movie to get  | 

Returns a JSON-format of one movie by its id
##### Response JSON-body example:
```http
{
    "Year of release": 2007,
    "Authors": [
        {
            "Id": 1,
            "firstName": "James",
            "lastName": "Cameron",
            "age": 44
        }
    ],
    "Rental finish date": "2003-09-04",
    "Id": 3,
    "Rental start date": "2003-12-02",
    "Name": "Avatar"
}
```
###### **HTTP Response Code:** 200 (OK)
#### Get movies filtered

```http
  GET http://localhost:8080/movies/search?${filters}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `filters` | `string` | **Required**. String of possible filters  | 

Returns a JSON-format of movies which match the given filters separated by commas with no spaces between them.
##### Possible filters:
 - name (*usage:* name='Avatar') -> `string`
 - year of release (*usage:* yearOfRelease=1998) -> `integer`
 - author Id (*usage:* authorId=123) -> `long`
 ##### Response JSON-body example:
```http
{
    "Year of release": 2007,
    "Authors": [
        {
            "Id": 1,
            "firstName": "James",
            "lastName": "Cameron",
            "age": 44
        }
    ],
    "Rental finish date": "2003-09-04",
    "Id": 3,
    "Rental start date": "2003-12-02",
    "Name": "Avatar"
}
```
 ###### **HTTP Response Code:** 200 (OK)
 #### Add a movie

```http
  POST http://localhost:8080/movies
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `-`       | `-`      | Adds a movie provided by JSON-body in request   | 

##### JSON-body example:
```http
{
    "Year of release": 2007,
    "Authors": [
        {
            "Id": 1,
            "firstName": "James",
            "lastName": "Cameron",
            "age": 44
        }
    ],
    "Rental finish date": "2003-09-04",
    "Id": 3,
    "Rental start date": "2003-12-02",
    "Name": "Avatar"
}
```
**NOTE**: You can add multiple authors in one movie
```http
{
    "Year of release": 2007,
    "Authors": [
        {
            "Id": 1,
            "firstName": "James",
            "lastName": "Cameron",
            "age": 44
        },
        {
            "Id": 2,
            "firstName": "Any",
            "lastName": "Author",
            "age": 29
        }
    ],
    "Rental finish date": "2003-09-04",
    "Id": 3,
    "Rental start date": "2003-12-02",
    "Name": "Avatar"
}
```
**NOTE**: You can skip the "Id" key
```http
{
    "Year of release": 2007,
    "Authors": [
        {
            "Id": 1,
            "firstName": "James",
            "lastName": "Cameron",
            "age": 44
        },
    ],
    "Rental finish date": "2003-09-04",
    "Rental start date": "2003-12-02",
    "Name": "Avatar"
}
```
###### **HTTP Response Code:** 201 (Created)
 #### Delete a movie by JSON-body in request
```http
  DELETE http://localhost:8080/movies
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `-`       | `-`      | Deletes a movie by id provided by JSON-body in request   |

##### JSON-body example:
```http
{
    "Id":100
}
```
###### **HTTP Response Code:** 204 (No content)
#### Delete a movie by request parameter
```http
  DELETE http://localhost:8080/movies?id=${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `long`   | **Required.** Id of movie to delete|

###### **HTTP Response Code:** 204 (No content)

#### Update a movie
```http
  PUT http://localhost:8080/movies
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `-`       | `-`      | Updates a movie by given JSON-body in request|

##### JSON-body example:
```http
{
    "Year of release": 2007,
    "Authors": [
        {
            "Id": 1,
            "firstName": "Vladik",
            "lastName": "Savko",
            "age": 44
        }
    ],
    "Rental finish date": "2003-09-04",
    "Id": 3,
    "Rental start date": "2003-12-02",
    "Name": "Some movie"
}
```
**NOTE**: You can change the number of authors by JSON-body (new authors will be added, old authors will be deleted or changed)
```http
{
    "Year of release": 2007,
    "Authors": [
        {
            "Id": 1,
            "firstName": "Vladik",
            "lastName": "Savko",
            "age": 44
        },
        {
            "Id": 2,
            "firstName": "Ivan",
            "lastName": "Ivanov",
            "age": 33
        }
    ],
    "Rental finish date": "2003-09-04",
    "Id": 3,
    "Rental start date": "2003-12-02",
    "Name": "Some movie"
}
```
###### **HTTP Response Code:** 201 (Created)






### Authors
#### Get all authors

```http
  GET http://localhost:8080/authors
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `-`       | `-`      | Returns all authors (JSON) |

##### Response JSON-body example:
```http
{
    "First name": "Greta",
    "Movies": [
        {
            "Year of release": 2023,
            "Rental finish date": "2003-09-04",
            "Id": 2,
            "Rental start date": "2003-11-02",
            "Name": "Barbie"
        }
    ],
    "Last name": "Gerwig",
    "Age": 32,
    "Id": 3
}
```

**HTTP Response Code:** 200 (OK)

#### Get one author

```http
  GET http://localhost:8080/authors/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `long`   | **Required**. Id of author to get  | 

Returns a JSON-format of one author by its id
##### Response JSON-body example:
```http
{
    "First name": "Greta",
    "Movies": [
        {
            "Year of release": 2023,
            "Rental finish date": "2003-09-04",
            "Id": 2,
            "Rental start date": "2003-11-02",
            "Name": "Barbie"
        }
    ],
    "Last name": "Gerwig",
    "Age": 32,
    "Id": 3
}
```
###### **HTTP Response Code:** 200 (OK)
#### Get authors filtered

```http
  GET http://localhost:8080/authors/search?${filters}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `filters` | `string` | **Required**. String of possible filters  | 

Returns a JSON-format of authors which match the given filters separated by commas with no spaces between them.
##### Possible filters:
 - first name (*usage:* firstName=James) -> `string`
 - last name (*usage:* lastName=Cameron) -> `string`
 - age (*usage:* age=50) -> `integer`
 - movie Id (*usage:* movieId=123) -> `long`
 ##### Response JSON-body example:
```http
{
    "First name": "Greta",
    "Movies": [
        {
            "Year of release": 2023,
            "Rental finish date": "2003-09-04",
            "Id": 2,
            "Rental start date": "2003-11-02",
            "Name": "Barbie"
        }
    ],
    "Last name": "Gerwig",
    "Age": 32,
    "Id": 3
}
```
 ###### **HTTP Response Code:** 200 (OK)
 #### Add an author

```http
  POST http://localhost:8080/authors
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `-`       | `-`      | Adds an author provided by JSON-body in request| 

##### JSON-body example:
```http
{
    "First name": "Greta",
    "Movies": [
        {
            "Year of release": 2023,
            "Rental finish date": "2003-09-04",
            "Id": 2,
            "Rental start date": "2003-11-02",
            "Name": "Barbie"
        }
    ],
    "Last name": "Gerwig",
    "Age": 32,
    "Id": 3
}
```
**NOTE**: You can add multiple movies in one author
```http
{
    "First name": "Greta",
    "Movies": [
        {
            "Year of release": 2023,
            "Rental finish date": "2003-09-04",
            "Id": 2,
            "Rental start date": "2003-11-02",
            "Name": "Barbie"
        },
        {
            "Year of release": 2023,
            "Rental finish date": "2003-09-04",
            "Id": 100,
            "Rental start date": "2003-11-02",
            "Name": "Barbie 2"
        }
    ],
    "Last name": "Gerwig",
    "Age": 32,
    "Id": 3
}
```
**NOTE**: You can skip the "Id" key
```http
{
    "First name": "Greta",
    "Movies": [
        {
            "Year of release": 2023,
            "Rental finish date": "2003-09-04",
            "Id": 2,
            "Rental start date": "2003-11-02",
            "Name": "Barbie"
        }
    ],
    "Last name": "Gerwig",
    "Age": 32
}
```
###### **HTTP Response Code:** 201 (Created)
 #### Delete an author by JSON-body in request
```http
  DELETE http://localhost:8080/authors
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `-`       | `-`      | Deletes an author by id provided by JSON-body in request   |

##### JSON-body example:
```http
{
    "Id":100
}
```
**NOTE**: If author's movies don't have other authors, they'll also be deleted
###### **HTTP Response Code:** 204 (No content)
#### Delete an author by request parameter
```http
  DELETE http://localhost:8080/authors?id=${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `long`   | **Required.** Id of author to delete|

**NOTE**: If author's movies don't have other authors, they'll also be deleted
###### **HTTP Response Code:** 204 (No content)

#### Update an author
```http
  PUT http://localhost:8080/authors
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `-`       | `-`      | Updates an author by given JSON-body in request|

##### JSON-body example:
```http
{
    "First name": "James",
    "Id": 1,
    "Movies": [
        {
            "Year of release": 2007,
            "Rental finish date": "2003-09-04",
            "Id": 3,
            "Rental start date": "2003-12-02",
            "Name": "Some movie"
        }
    ],
    "Last name": "Cameron",
    "Age": 44
}
```
**NOTE**: You can change the number of movies by JSON-body (new movies will be added, old movies will be deleted or changed)
```http
{
    "First name": "James",
    "Id": 1,
    "Movies": [
        {
            "Year of release": 2007,
            "Rental finish date": "2003-09-04",
            "Id": 3,
            "Rental start date": "2003-12-02",
            "Name": "Some movie"
        },
        {
            "Year of release": 2007,
            "Rental finish date": "2003-09-04",
            "Id": 4,
            "Rental start date": "2003-12-02",
            "Name": "Some movie 2"
        }
    ],
    "Last name": "Cameron",
    "Age": 44
}
```
###### **HTTP Response Code:** 201 (Created)

### Postman requests collection

You can also import the collection of all HTTP-requests for Postman by downloading [Collection](https://github.com/Vlad-Savko/application_task/blob/master/Application%20Task.postman_collection.json)
