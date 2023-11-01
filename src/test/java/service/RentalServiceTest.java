package service;

import com.application_task.app.entity.Movie;
import com.application_task.app.entity.Rental;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.service.MovieService;
import com.application_task.app.service.RentalService;
import com.application_task.app.service.impl.MovieServiceImpl;
import com.application_task.app.service.impl.RentalServiceImpl;
import com.application_task.app.util.Constants;
import com.application_task.app.util.PropertiesLoader;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Rental service + dao test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RentalServiceTest {
    private static RentalService rentalService;
    private static Rental rental;
    private static MovieService movieService;


    @BeforeAll
    static void init() {
        rentalService = new RentalServiceImpl(
                PropertiesLoader.getProperty(Constants.USER_PROPERTY_KEY),
                PropertiesLoader.getProperty(Constants.PASSWORD_PROPERTY_KEY)
        );
        rental = new Rental(LocalDate.now(), LocalDate.now());
    }

    @Test
    @DisplayName("Test creating correct rental")
    @Order(1)
    void testCreatingCorrectRental() throws DatabaseException {
        assertEquals(100, rentalService.create(rental));
    }

    @Test
    @DisplayName("Test incrementing id")
    @Order(2)
    void testIncrementingId() throws DatabaseException {
        assertEquals(101, rentalService.create(rental));
        assertEquals(102, rentalService.create(rental));
        assertEquals(103, rentalService.create(rental));
        assertEquals(104, rentalService.create(rental));
    }

    @Test
    @DisplayName("Test updating rental")
    @Order(3)
    void testUpdatingRental() throws DatabaseException {
        movieService = new MovieServiceImpl(
                PropertiesLoader.getProperty(Constants.USER_PROPERTY_KEY),
                PropertiesLoader.getProperty(Constants.PASSWORD_PROPERTY_KEY)
        );
        Movie movie = new Movie(
                1L,
                "other/mappers/testFiles/testAuthor",
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
