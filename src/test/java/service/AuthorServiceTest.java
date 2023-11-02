package service;

import com.application_task.app.db.connection.ConnectionPoolImpl;
import com.application_task.app.db.dao.impl.AuthorDaoImpl;
import com.application_task.app.entity.Author;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.service.AuthorService;
import com.application_task.app.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Author service + dao test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorServiceTest extends DatabaseConnector {
    private static AuthorService authorService;
    private static final AuthorDaoImpl authorDao = Mockito.mock(AuthorDaoImpl.class);

    @BeforeAll
    static void init() throws SQLException, IOException {
        postgres.start();
        authorService = new AuthorServiceImpl(
                postgres.getUsername(),
                postgres.getPassword(),
                postgres.getJdbcUrl()
        );

        connectionPool = new ConnectionPoolImpl(postgres.getUsername(), postgres.getPassword(), postgres.getJdbcUrl());
        fillPostgreDb();
    }

    @AfterAll
    static void drop() {
        postgres.stop();
    }

    @Test
    @DisplayName("Test creating author by correct dto")
    @Order(1)
    void correctCreationTest() throws DatabaseException {
        Author author = new Author(
                123L,
                "Name",
                "Surname",
                100);
        assertEquals(123L, authorService.create(author));
    }

    @Test
    @DisplayName("Test creating author with the same id")
    @Order(2)
    void duplicateMoviesTest() throws DatabaseException {
        Author author = new Author(
                123L,
                "Name",
                "Surname",
                100);
        assertEquals(1, authorService.getAll().size());
    }

    @Test
    @DisplayName("Get non-existing author by id")
    @Order(3)
    void getNonExistingMovieByIdTest() throws DatabaseException {
        assertTrue(authorService.get(-234L).isEmpty());
    }

    @Test
    @DisplayName("Get existing author by id")
    @Order(4)
    void getExistingMovieByIdTest() throws DatabaseException {
        assertTrue(authorService.get(123L).isPresent());
    }

    @Test
    @DisplayName("Get all authors")
    @Order(5)
    void getAllExistingMoviesTest() throws DatabaseException {
        Mockito.doReturn(Collections.singletonList("other/mappers/testFiles/testAuthor")).when(authorDao).getAll();
        assertEquals(1, authorService.getAll().size());
    }

    @Test
    @DisplayName("Update an author")
    @Order(6)
    void updateMovieTest() throws DatabaseException {
        Author author = new Author(
                123L,
                "NAME",
                "Surname",
                100);
        authorService.update(author);
        assertEquals("NAME", authorService.get(123L).get().firstName());
    }

    @Test
    @DisplayName("Delete an author")
    @Order(7)
    void deleteMovieTest() throws DatabaseException {
        authorService.delete(123L);
        assertTrue(authorService.get(123L).isEmpty());
    }
}
