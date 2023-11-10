package com.application_task.app.web;

import com.application_task.app.controllers.WebController;
import com.sun.net.httpserver.HttpExchange;

/**
 * Represents a wrapper of {@link WebController} which is used for processing {@link HttpExchange}s
 */
public record Handler(WebController controller) {
    /**
     * Provides this {@link WebController} with given {@code httpExchange}
     *
     * @param httpExchange {@link HttpExchange} to apply to this controller
     */
    public void handle(final HttpExchange httpExchange) {
        controller.execute(httpExchange);
        httpExchange.close();
    }
}
