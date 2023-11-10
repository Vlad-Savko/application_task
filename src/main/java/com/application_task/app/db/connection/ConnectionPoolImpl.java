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

    /**
     * Constructs a {@link ConnectionPool} implementation with the given credentials
     *
     * @param user        username to connect to datasource
     * @param password    password to connect to datasource
     * @param databaseUrl database URL to connect to datasource
     */
    public ConnectionPoolImpl(String user, String password, String databaseUrl) {
        initCredentials(user, password, databaseUrl);
    }

    /**
     * Constructs a {@link ConnectionPool} implementation with the given credentials, but with database URL from properties file
     *
     * @param user     username to connect to datasource
     * @param password password to connect to datasource
     */
    public ConnectionPoolImpl(String user, String password) {
        initCredentials(user, password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() {
        return createConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUser() {
        return this.user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Creates connection with the datasource defined by this credentials
     */
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

    /**
     * Sets the database connection credentials
     */
    private void initCredentials(String user, String password, String databaseUrl) {
        this.user = user;
        this.password = password;
        this.databaseUrl = databaseUrl;
    }

    /**
     * Sets the database connection credentials except database URL - it's taken from properties file
     */
    private void initCredentials(String user, String password) {
        this.user = user;
        this.password = password;
        this.databaseUrl = PropertiesLoader.getProperty(Constants.DB_URL_PROPERTY_KEY);
    }
}
