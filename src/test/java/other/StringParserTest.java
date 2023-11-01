package other;

import com.application_task.app.exception.WrongSearchFiltersException;
import com.application_task.app.util.StringParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test string parsing for params")
public class StringParserTest {
    @Test
    @DisplayName("Test valid params for movies")
    void testValidParamsForMovie() throws WrongSearchFiltersException {
        final String str = "name=aaa,yearOfRelease=1876";
        Map<String, String> paramsMap = StringParser.parseMovieFilters(str, StringParser.Type.MOVIE);

        assertAll(
                () -> assertTrue(paramsMap.containsKey("name")),
                () -> assertTrue(paramsMap.containsKey("yearOfRelease")),
                () -> assertEquals("aaa", paramsMap.get("name")),
                () -> assertEquals("1876", paramsMap.get("yearOfRelease"))
        );
    }

    @Test
    @DisplayName("Test invalid params for movies")
    void testInvalidParamsForMovie() {
        final String str = "name=anyString,yearOfRelease";
        assertThrows(
                WrongSearchFiltersException.class,
                () -> StringParser.parseMovieFilters(str, StringParser.Type.MOVIE)
        );
    }

    @Test
    @DisplayName("Test valid params for authors")
    void testValidParamsForAuthor() throws WrongSearchFiltersException {
        final String str = "firstName=aaa,lastName=bbb,age=100";
        Map<String, String> paramsMap = StringParser.parseMovieFilters(str, StringParser.Type.AUTHOR);

        assertAll(
                () -> assertTrue(paramsMap.containsKey("firstName")),
                () -> assertTrue(paramsMap.containsKey("lastName")),
                () -> assertTrue(paramsMap.containsKey("age")),
                () -> assertEquals("aaa", paramsMap.get("firstName")),
                () -> assertEquals("bbb", paramsMap.get("lastName")),
                () -> assertEquals("100", paramsMap.get("age"))
        );
    }

    @Test
    @DisplayName("Test invalid params for authors")
    void testInvalidParamsForAuthor() {
        final String str = "firstName=aaa,stName=bbb;,age=100";
        assertThrows(
                WrongSearchFiltersException.class,
                () -> StringParser.parseMovieFilters(str, StringParser.Type.AUTHOR)
        );
    }
}
