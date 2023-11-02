package com.application_task.app.db.connection;

import com.application_task.app.util.Constants;
import com.application_task.app.util.PropertiesLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPoolImpl implements ConnectionPool {
    private String user;
    private String password;
    private String databaseUrl;

    public ConnectionPoolImpl(String user, String password, String databaseUrl) {
        initCredentials(user, password, databaseUrl);
    }

    public ConnectionPoolImpl(String user, String password) {
        initCredentials(user, password);
    }

    @Override
    public Connection getConnection() {
        return createConnection();
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    private Connection createConnection() {
        try {
            Class.forName(Constants.Db.SQL_DRIVER);
            return DriverManager.getConnection(
                    this.databaseUrl,
                    this.user,
                    this.password);
        } catch (SQLException e) {
            throw new RuntimeException(Constants.Db.CONNECTION_ERROR, e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(Constants.Db.DRIVER_ERROR, e);
        }
    }

    private void initCredentials(String user, String password, String databaseUrl) {
        this.user = user;
        this.password = password;
        this.databaseUrl = databaseUrl;
    }

    private void initCredentials(String user, String password) {
        this.user = user;
        this.password = password;
        this.databaseUrl = PropertiesLoader.getProperty(Constants.DB_URL_PROPERTY_KEY);
    }
}
