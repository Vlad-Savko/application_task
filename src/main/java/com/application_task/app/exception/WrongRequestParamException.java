package com.application_task.app.exception;

import java.io.IOException;

/**
 * Represents an exception which is caused by wrong request parameters
 *
 * @see IOException
 */
public class WrongRequestParamException extends IOException {
    /**
     * Constructs an exception with the given error message
     *
     * @param message message to apply in exception
     */
    public WrongRequestParamException(final String message) {
        super(message);
    }
}
