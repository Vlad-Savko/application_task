package controller;

import com.application_task.app.db.connection.ConnectionPoolImpl;
import com.application_task.app.util.Constants;
import com.application_task.app.web.Server;
import controller.client.WebClient;
import controller.wrappers.MovieControllerWrapper;
import org.junit.jupiter.api.*;
import service.DatabaseConnector;

import java.io.IOException;
import java.sql.SQLException;

import static com.application_task.app.util.Constants.HttpMethod.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@DisplayName("Test movie controller")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MovieControllerTest extends DatabaseConnector {
    private static Server server;
    private static WebClient webClient;

    @BeforeAll
    static void init() throws SQLException, IOException {
        postgres.start();
        server = new Server.Builder()
                .port(8081)
                .controller(
                        Constants.OfServer.MOVIE_CONTROLLER_CONTEXT_PATH,
                        new MovieControllerWrapper(
                                postgres.getUsername(),
                                postgres.getPassword(),
                                postgres.getJdbcUrl()
                        )
                )
                .build();
        connectionPool = new ConnectionPoolImpl(postgres.getUsername(), postgres.getPassword(), postgres.getJdbcUrl());
        fillPostgreDb();
        server.start();
    }

    @Test
    @DisplayName("Get all movies test(no movies in database)")
    @Order(1)
    public void getAllMoviesWithEmptyTableTest() {
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8081/movies")
                .requestMethod(GET)
                .requestUrl("")
                .build();
        webClient.sendRequest();
        assertEquals("", webClient.getResponse());
        assertEquals(200, webClient.getResponseCode());
    }


    @Test
    @DisplayName("Get all movies test")
    @Order(5)
    public void getAllMoviesTest() {
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8081/movies")
                .requestMethod(GET)
                .requestUrl("")
                .build();
        webClient.sendRequest();
        assertEquals("{\"Year of release\":2021,\"Authors\":[{\"id\":1,\"firstName\":\"Some\",\"lastName\":" +
                        "\"Author\",\"age\":22}],\"Rental finish date\":\"2003-09-04\",\"Id\":1,\"Rental start date\":" +
                        "\"2003-12-02\",\"Name\":\"How I met your mother\"}{\"Year of release\":2007,\"Authors\":[{\"id\":2," +
                        "\"firstName\":\"James\",\"lastName\":\"Cameron\",\"age\":44}],\"Rental finish date\":\"2003-09-04\",\"Id\":2," +
                        "\"Rental start date\":\"2003-12-02\",\"Name\":\"Avatar\"}",
                webClient.getResponse());
        assertEquals(200, webClient.getResponseCode());
    }

    @Test
    @DisplayName("Get one existing movie")
    @Order(3)
    public void getOneExistingMovie() {
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8081/movies")
                .requestMethod(GET)
                .requestUrl("/1")
                .build();
        webClient.sendRequest();
        assertEquals("{\"Year of release\":2021,\"Authors\":[{\"id\":1,\"firstName\":\"Some\",\"lastName\":" +
                        "\"Author\",\"age\":22}],\"Rental finish date\":\"2003-09-04\",\"Id\":1,\"Rental start date\":\"2003-12-02\"" +
                        ",\"Name\":\"How I met your mother\"}",
                webClient.getResponse());
        assertEquals(200, webClient.getResponseCode());
    }

    @Test
    @DisplayName("Get one non-existing movie")
    @Order(9)
    public void getOneNonExistingMovie() {
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8081/movies")
                .requestMethod(GET)
                .requestUrl("/1")
                .build();
        webClient.sendRequest();
        assertNull(webClient.getResponse());
    }

    @Test
    @DisplayName("Get movie using filters")
    @Order(7)
    public void getMovieUsingFilters() {
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8081/movies")
                .requestMethod(GET)
                .requestUrl("/search?yearOfRelease=2021")
                .build();
        webClient.sendRequest();
        assertEquals("{\"Year of release\":2021,\"Authors\":[{\"id\":1,\"firstName\":\"Some\"," +
                        "\"lastName\":\"Author\",\"age\":22}],\"Rental finish date\":\"2003-09-04\",\"Id\":1,\"Rental start date\"" +
                        ":\"2003-12-02\",\"Name\":\"How I met your mother\"}",
                webClient.getResponse());
    }

    @Test
    @DisplayName("Get non-existing movie using filters")
    @Order(6)
    public void getNonExistingMovieUsingFilters() {
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8081/movies")
                .requestMethod(GET)
                .requestUrl("/search?name=Fury")
                .build();
        webClient.sendRequest();
        assertEquals("", webClient.getResponse());
    }

    @Test
    @DisplayName("Post a movie without id")
    @Order(2)
    public void addMovieWithoutIdTest() {
        String content = """
                    {
                    "Year of release": 2021,
                    "Authors": [
                        {
                            "Id": 1,
                            "firstName": "Some",
                            "lastName": "Author",
                            "age": 22
                        }
                    ],
                    "Rental finish date": "2003-09-04",
                    "Rental start date": "2003-12-02",
                    "Name": "How I met your mother"
                }
                                """;
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8081/movies")
                .requestMethod(POST)
                .requestUrl("")
                .content(content)
                .build();
        webClient.sendRequest();
        assertEquals(201, webClient.getResponseCode());

        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8081/movies")
                .requestMethod(GET)
                .requestUrl("/1")
                .build();
        webClient.sendRequest();
        assertEquals("{\"Year of release\":2021,\"Authors\":[{\"id\":1,\"firstName\":" +
                        "\"Some\",\"lastName\":\"Author\",\"age\":22}],\"Rental finish date\":\"2003-09-04\"," +
                        "\"Id\":1,\"Rental start date\":\"2003-12-02\",\"Name\":\"How I met your mother\"}",
                webClient.getResponse());
        assertEquals(200, webClient.getResponseCode());
    }

    @Test
    @DisplayName("Post a movie")
    @Order(4)
    public void addMovieWithIdTest() {
        String content = """
                    {
                    "Year of release": 2007,
                    "Authors": [
                        {
                            "Id": 2,
                            "firstName": "James",
                            "lastName": "Cameron",
                            "age": 44
                        }
                    ],
                    "Rental finish date": "2003-09-04",
                    "Id": 2,
                    "Rental start date": "2003-12-02",
                    "Name": "Avatar"
                }
                                """;
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8081/movies")
                .requestMethod(POST)
                .requestUrl("")
                .content(content)
                .build();
        webClient.sendRequest();
        assertEquals(201, webClient.getResponseCode());

        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8081/movies")
                .requestMethod(GET)
                .requestUrl("/2")
                .build();
        webClient.sendRequest();
        assertEquals("{\"Year of release\":2007,\"Authors\":[{\"id\":2,\"firstName\":" +
                        "\"James\",\"lastName\":\"Cameron\",\"age\":44}],\"Rental finish date\":\"2003-09-04\"," +
                        "\"Id\":2,\"Rental start date\":\"2003-12-02\",\"Name\":\"Avatar\"}",
                webClient.getResponse());
        assertEquals(200, webClient.getResponseCode());
    }

    @Test
    @DisplayName("Delete a movie")
    @Order(8)
    void deleteMovieTest() {
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8081/movies")
                .requestMethod(DELETE)
                .requestUrl("?id=1")
                .build();
        webClient.sendRequest();
        assertEquals(204, webClient.getResponseCode());
        assertEquals("", webClient.getResponse());
    }

    @Test
    @DisplayName("Update a movie")
    @Order(10)
    void updateMovieNameAndYearOfRelease() {
        String content = """
                    {
                    "Year of release": 1111,
                    "Authors": [
                        {
                            "Id": 2,
                            "firstName": "James",
                            "lastName": "Cameron",
                            "age": 44
                        }
                    ],
                    "Rental finish date": "2003-09-04",
                    "Id": 2,
                    "Rental start date": "2003-12-02",
                    "Name": "Avatar testAuthor"
                }
                                """;
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8081/movies")
                .requestMethod(PUT)
                .requestUrl("")
                .content(content)
                .build();
        webClient.sendRequest();
        assertEquals(201, webClient.getResponseCode());

        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8081/movies")
                .requestMethod(GET)
                .requestUrl("/2")
                .build();
        webClient.sendRequest();
        assertEquals("{\"Year of release\":1111,\"Authors\":[{\"id\":2,\"firstName\":" +
                        "\"James\",\"lastName\":\"Cameron\",\"age\":44}],\"Rental finish date\":\"2003-09-04\"," +
                        "\"Id\":2,\"Rental start date\":\"2003-12-02\",\"Name\":\"Avatar testAuthor\"}",
                webClient.getResponse());
        assertEquals(200, webClient.getResponseCode());
    }
}
