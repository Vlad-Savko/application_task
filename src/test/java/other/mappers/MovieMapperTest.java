package other.mappers;

import com.application_task.app.entity.Author;
import com.application_task.app.entity.Movie;
import com.application_task.app.entity.Rental;
import com.application_task.app.util.mappers.MovieMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsExchange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@DisplayName("Movie mapper testAuthor")
public class MovieMapperTest {
    private final HttpExchange httpExchange = Mockito.mock(HttpsExchange.class);

    @Test
    @DisplayName("Test converting movie to JSON")
    void testConvertingMovieToJson() {
        List<Author> relatedAuthors = List.of(
                new Author(1L, "name 1", "last name 1", 10),
                new Author(2L, "name 2", "last name 2", 20),
                new Author(3L, "name 3", "last name 3", 30)
        );

        Movie movie = new Movie(
                100L,
                "testName",
                1996,
                relatedAuthors,
                new Rental(
                        LocalDate.now(),
                        LocalDate.now()
                )
        );
        assertDoesNotThrow(() -> MovieMapper.toJson(movie));
    }

    @Test
    @DisplayName("Test converting JSON to movie")
    void testConvertingJsonToMovie() throws FileNotFoundException {
        when(httpExchange.getRequestBody())
                .thenReturn(
                        new FileInputStream(
                                "C:\\Users\\VladislavSavko\\javaProjects\\application_task\\src\\test\\java\\other\\mappers\\testFiles\\testMovie"
                        )
                );
        assertTrue(MovieMapper.fromJson(httpExchange).isPresent());
    }

    @Test
    @DisplayName("Test converting invalid JSON to movie")
    void testConvertingInvalidJsonToMovie() throws FileNotFoundException {
        when(httpExchange.getRequestBody())
                .thenReturn(
                        new FileInputStream(
                                "C:\\Users\\VladislavSavko\\javaProjects\\application_task\\src\\test\\java\\other\\mappers\\testFiles\\testWrongMovie"
                        )
                );
        assertTrue(MovieMapper.fromJson(httpExchange).isEmpty());
    }
}
