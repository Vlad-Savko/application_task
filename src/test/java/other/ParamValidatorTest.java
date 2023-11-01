package other;

import com.application_task.app.util.ParamValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Params validator test")
public class ParamValidatorTest {
    @Test
    @DisplayName("Test movie params (valid)")
    void testMovieParamsSuccessfully() {
        Map<String, String> testMap = Map.of(
                "key one", "value one", "yearOfRelease", "1994"
        );
        assertTrue(ParamValidator.checkMovieParams(testMap));
    }

    @Test
    @DisplayName("Test movie params (invalid)")
    void testMovieParamsUnsuccessfully() {
        Map<String, String> testMap = Map.of(
                "key one", "value one", "yearOfRelease", "aaaa"
        );
        assertFalse(ParamValidator.checkMovieParams(testMap));
    }

    @Test
    @DisplayName("Test author params (valid)")
    void testAuthorParamsSuccessfully() {
        Map<String, String> testMap = Map.of(
                "key one", "value one", "age", "22"
        );
        assertTrue(ParamValidator.checkAuthorParams(testMap));
    }

    @Test
    @DisplayName("Test author params (invalid)")
    void testAuthorParamsUnsuccessfully() {
        Map<String, String> testMap = Map.of(
                "key one", "value one", "age", "10"
        );
        assertFalse(ParamValidator.checkAuthorParams(testMap));
    }
}
