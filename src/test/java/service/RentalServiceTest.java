package service;

import com.application_task.app.db.connection.ConnectionPoolImpl;
import com.application_task.app.entity.Movie;
import com.application_task.app.entity.Rental;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.service.MovieService;
import com.application_task.app.service.RentalService;
import com.application_task.app.service.impl.MovieServiceImpl;
import com.application_task.app.service.impl.RentalServiceImpl;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Rental service + dao test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RentalServiceTest extends DatabaseConnector {
    private static RentalService rentalService;
    private static Rental rental;
    private static MovieService movieService;


    @BeforeAll
    static void init() throws SQLException, IOException {
        postgres.start();
        rentalService = new RentalServiceImpl(
                postgres.getUsername(),
                postgres.getPassword(),
                postgres.getJdbcUrl()
        );
        rental = new Rental(LocalDate.now(), LocalDate.now());

        connectionPool = new ConnectionPoolImpl(postgres.getUsername(), postgres.getPassword(), postgres.getJdbcUrl());
        fillPostgreDb();
    }

    @AfterAll
    static void drop() {
        postgres.stop();
    }

    @Test
    @DisplayName("Test creating correct rental")
    @Order(1)
    void testCreatingCorrectRental() throws DatabaseException {
        assertEquals(102, rentalService.create(rental));
    }

    @Test
    @DisplayName("Test incrementing id")
    @Order(2)
    void testIncrementingId() throws DatabaseException {
        assertEquals(103, rentalService.create(rental));
        assertEquals(104, rentalService.create(rental));
        assertEquals(105, rentalService.create(rental));
        assertEquals(106, rentalService.create(rental));
    }

    @Test
    @DisplayName("Test updating rental")
    @Order(3)
    void testUpdatingRental() throws DatabaseException {
        movieService = new MovieServiceImpl(
                postgres.getUsername(),
                postgres.getPassword(),
                postgres.getJdbcUrl()
        );
        Movie movie = new Movie(
                1L,
                "testFiles/testAuthor",
                1996,
                Collections.emptyList(),
                rental
        );
        movieService.create(movie);
        Rental updatedRental = new Rental(
                LocalDate.parse("2003-11-02"),
                LocalDate.now()
        );
        rentalService.update(updatedRental, movie.id());

        assertEquals(
                movieService.get(movie.id())
                        .get()
                        .rental()
                        .start(),
                LocalDate.parse("2003-11-02"));
    }
}
