package com.application_task.app.util.mappers;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Represents a mapper, which maps objects to/from JSON
 */
public class Mapper {

    /**
     * Parses id from {@link HttpExchange} JSON response body
     *
     * @param httpExchange {@link HttpExchange} to parse id from
     * @return {@code id} or {@code -1} if an error occurs
     */
    protected static long idFromJson(final HttpExchange httpExchange) {
        long id = -1;
        try (InputStream requestBody = httpExchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody, StandardCharsets.UTF_8))) {
            if (reader.ready()) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                id = convertJsonToLong(jsonElement);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException | JsonIOException | JsonSyntaxException ignored) {
        }
        return id;
    }

    /**
     * Converts JSON element to long value
     *
     * @param jsonElement {@link JsonElement} to convert
     * @return {@code long value}
     */
    private static long convertJsonToLong(JsonElement jsonElement) {
        com.google.gson.JsonObject data = jsonElement.getAsJsonObject();
        return data.get("Id").getAsLong();
    }
}
