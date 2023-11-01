package com.application_task.app.db.connection;

import java.sql.Connection;

public interface ConnectionPool {
    Connection getConnection();

    String getUser();

    String getPassword();
}
