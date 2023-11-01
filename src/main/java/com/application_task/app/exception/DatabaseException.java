package com.application_task.app.exception;

import java.io.IOException;

public class DatabaseException extends IOException {
    public DatabaseException(final String message) {
        super(message);
    }
}
