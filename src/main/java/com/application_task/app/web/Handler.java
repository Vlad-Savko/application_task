package com.application_task.app.web;

import com.application_task.app.controllers.WebController;
import com.sun.net.httpserver.HttpExchange;

public record Handler(WebController controller) {
    public void handle(final HttpExchange httpExchange) {
        controller.execute(httpExchange);
        httpExchange.close();
    }
}
