package other;

import com.application_task.app.db.connection.ConnectionPool;
import com.application_task.app.db.connection.ConnectionPoolImpl;
import com.application_task.app.util.Constants;
import com.application_task.app.util.PropertiesLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Database connection test")
public class ConnectionTest {
    private ConnectionPool connectionPool;

    @Test
    @DisplayName("Test connection with wrong credentials")
    void testConnectionWithWrongCredentials() {
        connectionPool = new ConnectionPoolImpl("wrong", "credentials");
        assertThrows(RuntimeException.class, () -> connectionPool.getConnection());
    }

    @Test
    @DisplayName("Test connection with valid credentials")
    void testConnectionWithValidCredentials() {
        connectionPool = new ConnectionPoolImpl(
                PropertiesLoader.getProperty(Constants.USER_PROPERTY_KEY),
                PropertiesLoader.getProperty(Constants.PASSWORD_PROPERTY_KEY)
        );
        assertDoesNotThrow(() -> {
            try (Connection test = connectionPool.getConnection()) {
            }
        });
    }
}
