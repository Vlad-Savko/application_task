package com.application_task.app.exception;

import java.io.IOException;

/**
 * Represents an exception which is caused by database errors
 *
 * @see IOException
 */
public class DatabaseException extends IOException {

    /**
     * Constructs an exception with the given error message
     *
     * @param message message to apply in exception
     */
    public DatabaseException(final String message) {
        super(message);
    }
}
