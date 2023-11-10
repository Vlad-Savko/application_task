package com.application_task.app.exception;

import java.io.IOException;

/**
 * Represents an exception which is caused by wrong search filters in request URL
 *
 * @see IOException
 */
public class WrongSearchFiltersException extends IOException {
    /**
     * Constructs an exception with the given error message
     *
     * @param message message to apply in exception
     */
    public WrongSearchFiltersException(final String message) {
        super(message);
    }
}
