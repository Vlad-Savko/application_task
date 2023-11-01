package com.application_task.app.db.connection;

import com.application_task.app.util.Constants;
import com.application_task.app.util.PropertiesLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ConnectionPoolImpl implements ConnectionPool {
    private final Queue<Connection> connections;
    private String user;
    private String password;
    private final int DEFAULT_POOL_SIZE = 10;

    @Override
    public Connection getConnection() {
        return createConnection(this.user, this.password);
    }

    public ConnectionPoolImpl(String user, String password) {
        initCredentials(user, password);

        this.connections = new ArrayBlockingQueue<>(DEFAULT_POOL_SIZE);
    }

    public ConnectionPoolImpl(String user, String password, int poolSize) {
        initCredentials(user, password);

        this.connections = new ArrayBlockingQueue<>(poolSize);
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    private Connection createConnection(String user, String password) {
        try {
            Class.forName(Constants.Db.SQL_DRIVER);
            return DriverManager.getConnection(
                    PropertiesLoader.getProperty(Constants.DB_URL_PROPERTY_KEY),
                    user,
                    password);
        } catch (SQLException e) {
            throw new RuntimeException(Constants.Db.CONNECTION_ERROR, e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(Constants.Db.DRIVER_ERROR, e);
        }
    }

    private void initCredentials(String user, String password) {
        this.user = user;
        this.password = password;
    }
}
