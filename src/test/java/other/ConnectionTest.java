package other;

import com.application_task.app.db.connection.ConnectionPoolImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.DatabaseConnector;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Database connection test")
public class ConnectionTest extends DatabaseConnector {
    @AfterAll
    static void drop() {
        postgres.stop();
    }

    @Test
    @DisplayName("Test connection with wrong credentials")
    void testConnectionWithWrongCredentials() {
        connectionPool = new ConnectionPoolImpl("wrong", "credentials");
        assertThrows(RuntimeException.class, () -> connectionPool.getConnection());
    }

    @Test
    @DisplayName("Test connection with valid credentials")
    void testConnectionWithValidCredentials() {
        postgres.start();
        connectionPool = new ConnectionPoolImpl(
                postgres.getUsername(),
                postgres.getPassword(),
                postgres.getJdbcUrl()
        );
        assertDoesNotThrow(() -> {
            try (Connection test = connectionPool.getConnection()) {
            }
        });
    }
}
