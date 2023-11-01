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

public class Mapper {
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

    private static long convertJsonToLong(JsonElement jsonElement) {
        com.google.gson.JsonObject data = jsonElement.getAsJsonObject();
        return data.get("Id").getAsLong();
    }
}
