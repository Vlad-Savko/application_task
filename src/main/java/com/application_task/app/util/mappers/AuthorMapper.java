package com.application_task.app.util.mappers;

import com.application_task.app.db.dto.AuthorDto;
import com.application_task.app.db.dto.MovieDto;
import com.application_task.app.entity.Rental;
import com.application_task.app.service.impl.AuthorServiceImpl;
import com.application_task.app.service.impl.MovieServiceImpl;
import com.application_task.app.util.Constants;
import com.github.cliftonlabs.json_simple.JsonObject;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@inheritDoc}
 */
public class AuthorMapper extends Mapper {
    private final static String ID = "Id";
    private final static String FIRST_NAME = "First name";
    private final static String LAST_NAME = "Last name";
    private final static String MOVIES = "Movies";
    private final static String AGE = "Age";

    /**
     * Converts author dto object to JSON format
     *
     * @param author {@link AuthorDto} to convert to JSON format
     * @return {@link JsonObject} representation of the given author
     */
    public static JsonObject toJson(AuthorDto author) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(ID, author.id());
        jsonObject.put(FIRST_NAME, author.firstName());
        jsonObject.put(LAST_NAME, author.lastName());
        jsonObject.put(MOVIES, author.relatedMovies().stream()
                .map(MovieMapper::toJson)
                .toList());
        jsonObject.put(AGE, author.age());

        return jsonObject;
    }

    /**
     * Parses author dto object from the given {@link HttpExchange}
     *
     * @param httpExchange {@link HttpExchange} to parse author dto object from
     * @return {@link Optional} of parsed author dto (empty optional if any error occurs)
     */
    public static Optional<AuthorDto> fromJson(final HttpExchange httpExchange) {
        AuthorDto author = null;
        try (InputStream requestBody = httpExchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody, StandardCharsets.UTF_8))) {
            if (reader.ready()) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                author = convertJsonToAuthor(jsonElement);
            }
        } catch (JsonIOException | JsonSyntaxException | NumberFormatException | IOException ignored) {
        }
        return Optional.ofNullable(author);
    }

    /**
     * Converts JSON element to author dto
     *
     * @param jsonElement {@link JsonElement} to convert to author dto
     * @return {@link AuthorDto} of the given JSON element
     * @throws IOException if there's an i/o error with {@code jsonElement}
     */
    private static AuthorDto convertJsonToAuthor(final JsonElement jsonElement) throws IOException {
        List<MovieDto> movies = new ArrayList<>();

        com.google.gson.JsonObject data = jsonElement.getAsJsonObject();
        com.google.gson.JsonArray moviesJsonArray = data.get("Movies").getAsJsonArray();
        for (int i = 0; i < moviesJsonArray.size(); i++) {
            com.google.gson.JsonObject movie = moviesJsonArray.get(i).getAsJsonObject();
            long id;
            try {
                id = movie.get("Id").getAsLong();
            } catch (NullPointerException npe) {
                id = MovieServiceImpl.getIdCounter();
                MovieServiceImpl.incIdCounter();
            }
            movies.add(new MovieDto(
                            id,
                            movie.get("Name").getAsString(),
                            movie.get("Year of release").getAsInt(),
                            new Rental(
                                    LocalDate.parse(movie.get("Rental start date").getAsString()),
                                    LocalDate.parse(movie.get("Rental finish date").getAsString())
                            )
                    )
            );
        }
        long id;
        try {
            id = data.get("Id").getAsLong();
        } catch (NullPointerException npe) {
            id = AuthorServiceImpl.getIdCounter();
            AuthorServiceImpl.incIdCounter();
        }
        int age = data.get("Age").getAsInt();
        if (age > 120 || age < 17) {
            throw new IOException(Constants.Message.ERROR_WRONG_AGE);
        }
        return new AuthorDto(
                id,
                data.get("First name").getAsString(),
                data.get("Last name").getAsString(),
                age,
                movies);
    }

    /**
     * {@inheritDoc}
     */
    public static long idFromJson(final HttpExchange httpExchange) {
        return Mapper.idFromJson(httpExchange);
    }
}
