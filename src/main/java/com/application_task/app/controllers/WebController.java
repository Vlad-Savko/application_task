package com.application_task.app.controllers;

import com.application_task.app.util.Constants;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class WebController {
    abstract public void execute(final HttpExchange httpExchange);

    protected void writeResponse(final HttpExchange httpExchange, List<JsonObject> jsonObjects, int status) throws IOException {
        Headers responseHeaders = httpExchange.getResponseHeaders();
        StringBuilder db = new StringBuilder();
        for (JsonObject jsonObject : jsonObjects) {
            db.append(String.format("%s%n", new Gson().toJson(jsonObject)));
        }
        responseHeaders.add("Content-type", "application/json");
        response(httpExchange, status, db.toString().getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream responseBody = httpExchange.getResponseBody()) {
            responseBody.write(db.toString().getBytes(StandardCharsets.UTF_8));
            responseBody.flush();
        }
    }

    private void response(final HttpExchange httpExchange, final int status, final int responseLength) {
        try {
            httpExchange.sendResponseHeaders(status, responseLength);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void writeErrorResponse(final HttpExchange httpExchange, String message, int status) throws IOException {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        response(httpExchange, status, messageBytes.length);
        try (OutputStream responseBody = httpExchange.getResponseBody()) {
            responseBody.write(messageBytes);
            responseBody.flush();
        }
    }

    protected void errorResponse(HttpExchange httpExchange, String ex, int status) {
        try {
            writeErrorResponse(httpExchange, ex, status);
        } catch (IOException exc) {
            throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, exc);
        }
    }
}
