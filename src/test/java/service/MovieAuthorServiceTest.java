package service;

import com.application_task.app.db.connection.ConnectionPoolImpl;
import com.application_task.app.entity.Author;
import com.application_task.app.entity.Movie;
import com.application_task.app.entity.Rental;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.service.AuthorService;
import com.application_task.app.service.MovieAuthorService;
import com.application_task.app.service.MovieService;
import com.application_task.app.service.impl.AuthorServiceImpl;
import com.application_task.app.service.impl.MovieAuthorServiceImpl;
import com.application_task.app.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("MovieAuthor service + dao test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MovieAuthorServiceTest extends DatabaseConnector {
    private static MovieAuthorService movieAuthorService;
    private static AuthorService authorService;
    private static List<Long> moviesIds;
    private static List<Long> authorsIds;
    private static MovieService movieService;

    @BeforeAll
    static void init() throws SQLException, IOException {
        postgres.start();
        moviesIds = List.of(1L, 2L, 3L, 4L);
        authorsIds = List.of(5L, 6L, 7L, 8L);
        String username = postgres.getUsername();
        String password = postgres.getPassword();
        String databaseUrl = postgres.getJdbcUrl();

        movieAuthorService = new MovieAuthorServiceImpl(
                username,
                password,
                databaseUrl
        );
        authorService = new AuthorServiceImpl(
                username,
                password,
                databaseUrl
        );
        movieService = new MovieServiceImpl(
                username,
                password,
                databaseUrl
        );

        connectionPool = new ConnectionPoolImpl(postgres.getUsername(), postgres.getPassword(), postgres.getJdbcUrl());
        fillPostgreDb();
    }

    @AfterAll
    static void drop() {
        postgres.stop();
    }

    @Test
    @DisplayName("Test binding author to movies")
    @Order(1)
    void testBindingAuthorToMovies() throws DatabaseException {
        Author author = new Author(100L,
                "Vlad",
                "Savko",
                20
        );
        authorService.create(author);
        movieAuthorService.bindAuthorToMovies(author.id(), moviesIds);

        assertEquals(4, authorService.getAsDto(100L).get().relatedMovies().size());
    }

    @Test
    @DisplayName("Test binding movie to authors")
    @Order(2)
    void testBindingMovieToAuthors() throws DatabaseException {
        Movie movie = new Movie(100L,
                "Test",
                2000,
                Collections.emptyList(),
                new Rental(LocalDate.now(), LocalDate.now())
        );
        movieService.create(movie);
        movieAuthorService.bindMovieToAuthors(movie.id(), authorsIds);

        assertEquals(4, movieService.get(100L).get().authors().size());
    }

    @Test
    @DisplayName("Test unbinding author from movies")
    @Order(3)
    void testUnbindingAuthorFromMovies() throws DatabaseException {
        movieAuthorService.unbindMoviesFromAuthor(100L, moviesIds);

        assertEquals(0, authorService.getAsDto(100L).get().relatedMovies().size());
    }

    @Test
    @DisplayName("Test unbinding movie from authors")
    @Order(4)
    void testUnbindingMovieFromAuthors() throws DatabaseException {
        movieAuthorService.unbindAuthorsFromMovie(100L, authorsIds);

        assertEquals(0, movieService.get(100L).get().authors().size());
    }
}
