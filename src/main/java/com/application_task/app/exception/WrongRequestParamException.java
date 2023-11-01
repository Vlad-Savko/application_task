package com.application_task.app.exception;

import java.io.IOException;

public class WrongRequestParamException extends IOException {
    public WrongRequestParamException(final String message) {
        super(message);
    }
}
