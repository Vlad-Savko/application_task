package other.mappers;

import com.application_task.app.db.dto.AuthorDto;
import com.application_task.app.db.dto.MovieDto;
import com.application_task.app.entity.Rental;
import com.application_task.app.util.mappers.AuthorMapper;
import com.sun.net.httpserver.HttpExchange;
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

@DisplayName("Author mapper testAuthor")
public class AuthorMapperTest {
    private final HttpExchange httpExchange = Mockito.mock(HttpExchange.class);

    @Test
    @DisplayName("Test converting author to JSON")
    void testConvertingAuthorToJson() {
        List<MovieDto> relatedMovies = List.of(
                new MovieDto(1L, "Test name 1", 2000, new Rental(LocalDate.now(), LocalDate.now())),
                new MovieDto(2L, "Test name 2", 2001, new Rental(LocalDate.now(), LocalDate.now())),
                new MovieDto(3L, "Test name 3", 2002, new Rental(LocalDate.now(), LocalDate.now()))
        );

        AuthorDto author = new AuthorDto(1000L, "first name", "last name", 33, relatedMovies);
        assertDoesNotThrow(() -> AuthorMapper.toJson(author));
    }

    @Test
    @DisplayName("Test converting JSON to author")
    void testConvertingJsonToAuthor() throws FileNotFoundException {
        when(httpExchange.getRequestBody())
                .thenReturn(
                        new FileInputStream(
                                "C:\\Users\\VladislavSavko\\javaProjects\\application_task\\src\\test\\java\\other\\mappers\\testFiles\\testAuthor"
                        )
                );
        assertTrue(AuthorMapper.fromJson(httpExchange).isPresent());
    }

    @Test
    @DisplayName("Test converting invalid JSON to author")
    void testConvertingInvalidJsonToAuthor() throws FileNotFoundException {
        when(httpExchange.getRequestBody())
                .thenReturn(
                        new FileInputStream(
                                "C:\\Users\\VladislavSavko\\javaProjects\\application_task\\src\\test\\java\\other\\mappers\\testFiles\\testWrongAuthor"
                        )
                );
        assertTrue(AuthorMapper.fromJson(httpExchange).isEmpty());
    }
}
