package other;

import com.application_task.app.web.Server;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Web server test")
public class WebServerTest {
    private static Server httpServer;

    @BeforeAll
    static void init() {
        httpServer = new Server.Builder().port(8080).build();
    }

    @Test
    @DisplayName("Test server starting and stopping")
    void testServerStart() {
        assertDoesNotThrow(() -> httpServer.start());
    }

    @Test
    @DisplayName("Test server stopping")
    void testServerStop() {
        assertDoesNotThrow(() -> httpServer.stop());
    }
}
