package com.application_task.app.db.connection;

import java.sql.Connection;

/**
 * Represents a connection pool, which is used for connecting to datasource
 */
public interface ConnectionPool {
    /**
     * Returns an instance of {@link Connection} to datasource provided by properties file or demand
     *
     * @return an instance of {@link Connection}
     */
    Connection getConnection();

    /**
     * Returns the username of connection to datasource
     *
     * @return the username of connection to datasource
     */
    String getUser();

    /**
     * Returns the password of connection to datasource
     *
     * @return the password of connection to datasource
     */
    String getPassword();
}
