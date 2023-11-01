package service;

import com.application_task.app.db.dao.impl.MovieDaoImpl;
import com.application_task.app.db.dto.MovieDto;
import com.application_task.app.entity.Author;
import com.application_task.app.entity.Movie;
import com.application_task.app.entity.Rental;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.service.MovieService;
import com.application_task.app.service.impl.MovieServiceImpl;
import com.application_task.app.util.Constants;
import com.application_task.app.util.PropertiesLoader;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Movie service + dao test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MovieServiceTest {
    private static MovieService movieService;
    private static final MovieDaoImpl movieDao = Mockito.mock(MovieDaoImpl.class);


    @BeforeAll
    static void init() {
        movieService = new MovieServiceImpl(
                PropertiesLoader.getProperty(Constants.USER_PROPERTY_KEY),
                PropertiesLoader.getProperty(Constants.PASSWORD_PROPERTY_KEY));
    }

    @Test
    @DisplayName("Test creating movie by correct dto")
    @Order(1)
    void correctCreationTest() throws DatabaseException {
        MovieDto movieDto = new MovieDto(
                123L, "Name", 2010,
                new Rental(LocalDate.now(), LocalDate.of(2003, Month.APRIL, 10)));
        assertEquals(123L, movieService.create(movieDto));
    }

    @Test
    @DisplayName("Test creating movie with the same id")
    @Order(2)
    void duplicateMoviesTest() throws DatabaseException {
        MovieDto movieDto = new MovieDto(
                123L, "Name", 2010,
                new Rental(LocalDate.now(), LocalDate.of(2003, Month.APRIL, 10)));
        assertEquals(-1L, movieService.create(movieDto));
    }

    @Test
    @DisplayName("Get non-existing movie by id")
    @Order(3)
    void getNonExistingMovieByIdTest() throws DatabaseException {
        assertTrue(movieService.get(-234L).isEmpty());
    }

    @Test
    @DisplayName("Get existing movie by id")
    @Order(4)
    void getExistingMovieByIdTest() throws DatabaseException {
        assertTrue(movieService.get(123L).isPresent());
    }

    @Test
    @DisplayName("Get all movies")
    @Order(5)
    void getAllExistingMoviesTest() throws DatabaseException {
        Mockito.doReturn(Collections.singletonList("other/mappers/testFiles/testAuthor")).when(movieDao).getAll();
        assertEquals(1, movieService.getAll().size());
    }

    @Test
    @DisplayName("Update a movie")
    @Order(6)
    void updateMovieTest() throws DatabaseException {
        Movie movie = new Movie(
                123L,
                "Name",
                2011,
                Collections.singletonList(
                        new Author(11L, "other/mappers/testFiles/testAuthor", "other/mappers/testFiles/testAuthor", 100)),
                new Rental(LocalDate.now(), LocalDate.of(2003, Month.APRIL, 10)));
        movieService.update(movie);
        assertEquals(2011, movieService.get(123L).get().yearOfRelease());
    }

    @Test
    @DisplayName("Delete a movie")
    @Order(7)
    void deleteMovieTest() throws DatabaseException {
        movieService.delete(123L);
        assertTrue(movieService.get(123L).isEmpty());
    }
}
