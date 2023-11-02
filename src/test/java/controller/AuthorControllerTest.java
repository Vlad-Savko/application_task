package controller;

import com.application_task.app.db.connection.ConnectionPoolImpl;
import com.application_task.app.util.Constants;
import com.application_task.app.web.Server;
import controller.client.WebClient;
import controller.wrappers.AuthorControllerWrapper;
import org.junit.jupiter.api.*;
import service.DatabaseConnector;

import java.io.IOException;
import java.sql.SQLException;

import static com.application_task.app.util.Constants.HttpMethod.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@DisplayName("Test author controller")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorControllerTest extends DatabaseConnector {
    private static Server server;
    private static WebClient webClient;

    @BeforeAll
    static void init() throws SQLException, IOException {
        postgres.start();
        server = new Server.Builder()
                .port(8082)
                .controller(
                        Constants.OfServer.AUTHOR_CONTROLLER_CONTEXT_PATH,
                        new AuthorControllerWrapper(
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

    @AfterAll
    static void drop() {
        server.stop();
        postgres.stop();
    }

    @Test
    @DisplayName("Get all authors test(no authors in database)")
    @Order(1)
    public void getAllAuthorsWithEmptyTableTest() {
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8082/authors")
                .requestMethod(GET)
                .requestUrl("")
                .build();
        webClient.sendRequest();
        assertEquals("", webClient.getResponse());
        assertEquals(200, webClient.getResponseCode());
    }


    @Test
    @DisplayName("Get all movies testAuthor")
    @Order(5)
    public void getAllAuthorsTest() {
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8082/authors")
                .requestMethod(GET)
                .requestUrl("")
                .build();
        webClient.sendRequest();
        assertEquals("{\"First name\":\"Vladik\",\"Id\":1,\"Movies\":[{\"Year of release\":2002,\"Rental finish date\"" +
                        ":\"2003-09-04\",\"Id\":1,\"Rental start date\":\"2003-11-02\",\"Name\":\"Java Programming\"}],\"Last name\"" +
                        ":\"Savko\",\"Age\":20}{\"First name\":\"Greta\",\"Id\":2,\"Movies\":[{\"Year of release\":2023,\"Rental finish date\"" +
                        ":\"2003-09-04\",\"Id\":2,\"Rental start date\":\"2003-11-02\",\"Name\":\"Barbie\"}],\"Last name\":\"Grewig\",\"Age\":" +
                        "32}",
                webClient.getResponse());
        assertEquals(200, webClient.getResponseCode());
    }

    @Test
    @DisplayName("Get one existing author")
    @Order(3)
    public void getOneExistingAuthor() {
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8082/authors")
                .requestMethod(GET)
                .requestUrl("/1")
                .build();
        webClient.sendRequest();
        assertEquals("{\"First name\":\"Vladik\",\"Id\":1,\"Movies\":[{\"Year of release\":2002,\"Rental finish date\"" +
                        ":\"2003-09-04\",\"Id\":1,\"Rental start date\":\"2003-11-02\",\"Name\":\"Java Programming\"}],\"Last name\"" +
                        ":\"Savko\",\"Age\":20}",
                webClient.getResponse());
        assertEquals(200, webClient.getResponseCode());
    }

    @Test
    @DisplayName("Get one non-existing author")
    @Order(9)
    public void getOneNonExistingAuthor() {
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8082/authors")
                .requestMethod(GET)
                .requestUrl("/2")
                .build();
        webClient.sendRequest();
        assertNull(webClient.getResponse());
    }

    @Test
    @DisplayName("Get author using filters")
    @Order(7)
    public void getAuthorUsingFilters() {
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8082/authors")
                .requestMethod(GET)
                .requestUrl("/search?lastName=Grewig")
                .build();
        webClient.sendRequest();
        assertEquals("{\"First name\":\"Greta\",\"Id\":2,\"Movies\":[{\"Year of release\":2023," +
                        "\"Rental finish date\":\"2003-09-04\",\"Id\":2,\"Rental start date\":\"2003-11-02\",\"Name\"" +
                        ":\"Barbie\"}],\"Last name\":\"Grewig\",\"Age\":32}",
                webClient.getResponse());
    }

    @Test
    @DisplayName("Get non-existing author using filters")
    @Order(6)
    public void getNonExistingAuthorUsingFilters() {
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8082/authors")
                .requestMethod(GET)
                .requestUrl("/search?firstName=Anna")
                .build();
        webClient.sendRequest();
        assertEquals("", webClient.getResponse());
    }

    @Test
    @DisplayName("Add an author without id")
    @Order(2)
    public void addAuthorWithoutIdTest() {
        String content = """
                    {
                    "First name": "Vladik",
                    "Movies": [
                        {
                            "Year of release": 2002,
                            "Rental finish date": "2003-09-04",
                            "Id": 1,
                            "Rental start date": "2003-11-02",
                            "Name": "Java Programming"
                        }
                    ],
                    "Last name": "Savko",
                    "Age": 20
                }
                                """;
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8082/authors")
                .requestMethod(POST)
                .requestUrl("")
                .content(content)
                .build();
        webClient.sendRequest();
        assertEquals(201, webClient.getResponseCode());

        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8082/authors")
                .requestMethod(GET)
                .requestUrl("/1")
                .build();
        webClient.sendRequest();
        assertEquals("{\"First name\":\"Vladik\",\"Id\":1,\"Movies\":[{\"Year of release\":2002," +
                        "\"Rental finish date\":\"2003-09-04\",\"Id\":1,\"Rental start date\":\"2003-11-02\"," +
                        "\"Name\":\"Java Programming\"}],\"Last name\":\"Savko\",\"Age\":20}",
                webClient.getResponse());
        assertEquals(200, webClient.getResponseCode());
    }

    @Test
    @DisplayName("Add an author")
    @Order(4)
    public void addAuthorWithIdTest() {
        String content = """
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
                    "Last name": "Grewig",
                    "Age": 32,
                    "Id": 2
                }
                                """;
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8082/authors")
                .requestMethod(POST)
                .requestUrl("")
                .content(content)
                .build();
        webClient.sendRequest();
        assertEquals(201, webClient.getResponseCode());

        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8082/authors")
                .requestMethod(GET)
                .requestUrl("/2")
                .build();
        webClient.sendRequest();
        assertEquals("{\"First name\":\"Greta\",\"Id\":2,\"Movies\":[{\"Year of release\":2023," +
                        "\"Rental finish date\":\"2003-09-04\",\"Id\":2,\"Rental start date\":\"2003-11-02\",\"Name\"" +
                        ":\"Barbie\"}],\"Last name\":\"Grewig\",\"Age\":32}",
                webClient.getResponse());
        assertEquals(200, webClient.getResponseCode());
    }

    @Test
    @DisplayName("Delete an author")
    @Order(8)
    void deleteMovieTest() {
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8082/authors")
                .requestMethod(DELETE)
                .requestUrl("?id=2")
                .build();
        webClient.sendRequest();
        assertEquals(204, webClient.getResponseCode());
        assertEquals("", webClient.getResponse());
    }

    @Test
    @DisplayName("Update an author")
    @Order(10)
    void updateAuthorsFirstNameAndLastNameTest() {
        String content = """
                    {
                    "First name": "UPDATED FIRST NAME",
                        "Movies": [
                            {
                                "Year of release": 2002,
                            "Rental finish date": "2003-09-04",
                            "Id": 1,
                            "Rental start date": "2003-11-02",
                            "Name": "Java Programming"
                            }
                        ],
                        "Last name": "UPDATED LAST NAME",
                        "Age": 32,
                        "Id": 1
                }
                                """;
        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8082/authors")
                .requestMethod(PUT)
                .requestUrl("")
                .content(content)
                .build();
        webClient.sendRequest();
        assertEquals(201, webClient.getResponseCode());

        webClient = new WebClient.Builder()
                .contextPath("http://localhost:8082/authors")
                .requestMethod(GET)
                .requestUrl("/1")
                .build();
        webClient.sendRequest();
        assertEquals("{\"First name\":\"UPDATED FIRST NAME\",\"Id\":1,\"Movies\":[{\"Year of release\":2002," +
                        "\"Rental finish date\":\"2003-09-04\",\"Id\":1,\"Rental start date\":\"2003-11-02\",\"Name\":" +
                        "\"Java Programming\"}],\"Last name\":\"UPDATED LAST NAME\",\"Age\":32}",
                webClient.getResponse());
        assertEquals(200, webClient.getResponseCode());
    }
}
