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

/**
 * Represents an abstract controller, which has methods for processing {@link HttpExchange}:<br/>
 * - execute CRUD methods using the information from HTTP request<br/>
 * - send HTTP response
 */
public abstract class WebController {
    /**
     * Processes {@link HttpExchange} and sends response
     *
     * @param httpExchange an http exchange.
     */
    abstract public void execute(final HttpExchange httpExchange);

    /**
     * Writes response to {@link HttpExchange}'s response body
     *
     * @param httpExchange an http exchange.
     * @param jsonObjects  a list of JSON-objects to write to response.
     * @param status       an http response status code to send.
     * @throws IOException if there's an i/o error with {@link HttpExchange}
     */
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

    /**
     * Sends response headers to http response
     *
     * @param httpExchange   an http exchange.
     * @param status         an http response status code to send.
     * @param responseLength a response length.
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpExchange.html#sendResponseHeaders(int,long)"/>sendResponseHeaders
     */
    private void response(final HttpExchange httpExchange, final int status, final int responseLength) {
        try {
            httpExchange.sendResponseHeaders(status, responseLength);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes error response
     *
     * @param httpExchange an http exchange.
     * @param message      a message to send as error message.
     * @param status       an http response status code to send.
     * @throws IOException if there's an i/o error with {@link HttpExchange}
     */
    protected void writeErrorResponse(final HttpExchange httpExchange, String message, int status) throws IOException {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        response(httpExchange, status, messageBytes.length);
        try (OutputStream responseBody = httpExchange.getResponseBody()) {
            responseBody.write(messageBytes);
            responseBody.flush();
        }
    }

    /**
     * Writes error response
     *
     * @param httpExchange an http exchange.
     * @param ex           a message to send as error message.
     * @param status       an http response status code to send.
     */
    protected void errorResponse(HttpExchange httpExchange, String ex, int status) {
        try {
            writeErrorResponse(httpExchange, ex, status);
        } catch (IOException exc) {
            throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, exc);
        }
    }
}
