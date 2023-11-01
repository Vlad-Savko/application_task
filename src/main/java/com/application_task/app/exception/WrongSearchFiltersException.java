package com.application_task.app.exception;

import java.io.IOException;

public class WrongSearchFiltersException extends IOException {
    public WrongSearchFiltersException(final String message) {
        super(message);
    }
}
