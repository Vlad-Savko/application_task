package com.application_task.app.util.mappers;

import com.application_task.app.db.dto.MovieDto;
import com.application_task.app.entity.Author;
import com.application_task.app.entity.Movie;
import com.application_task.app.entity.Rental;
import com.application_task.app.service.impl.MovieServiceImpl;
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

public class MovieMapper extends Mapper {
    private final static String ID = "Id";
    private final static String NAME = "Name";
    private final static String YEAR_OF_RELEASE = "Year of release";
    private final static String AUTHORS = "Authors";
    private static final String RENTAL_START_DATE = "Rental start date";
    private static final String RENTAL_FINISH_DATE = "Rental finish date";

    public static JsonObject toJson(Movie movie) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(ID, movie.id());
        jsonObject.put(NAME, movie.name());
        jsonObject.put(YEAR_OF_RELEASE, movie.yearOfRelease());
        jsonObject.put(AUTHORS, movie.authors());
        jsonObject.put(RENTAL_START_DATE, movie.rental().start().toString());
        jsonObject.put(RENTAL_FINISH_DATE, movie.rental().finish().toString());

        return jsonObject;
    }

    public static JsonObject toJson(MovieDto movieDto) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(ID, movieDto.id());
        jsonObject.put(NAME, movieDto.name());
        jsonObject.put(YEAR_OF_RELEASE, movieDto.yearOfRelease());
        jsonObject.put(RENTAL_START_DATE, movieDto.rental().start().toString());
        jsonObject.put(RENTAL_FINISH_DATE, movieDto.rental().finish().toString());

        return jsonObject;
    }

    public static Optional<Movie> fromJson(final HttpExchange httpExchange) {
        Movie movie = null;
        try (InputStream requestBody = httpExchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody, StandardCharsets.UTF_8))) {
            if (reader.ready()) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                movie = convertJsonToMovie(jsonElement);
            }
        } catch (JsonIOException | JsonSyntaxException | NumberFormatException ignored) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(movie);
    }

    private static Movie convertJsonToMovie(final JsonElement jsonElement) {
        com.google.gson.JsonObject data = jsonElement.getAsJsonObject();
        try {
            Rental rental = new Rental(LocalDate.parse(data.get("Rental start date").getAsString()),
                    LocalDate.parse(data.get("Rental finish date").getAsString()));
            List<Author> authors = new ArrayList<>();


            com.google.gson.JsonArray authorsJsonArray = data.get("Authors").getAsJsonArray();
            for (int i = 0; i < authorsJsonArray.size(); i++) {
                com.google.gson.JsonObject a = authorsJsonArray.get(i).getAsJsonObject();
                authors.add(new Author(
                                a.get("Id").getAsLong(),
                                a.get("firstName").getAsString(),
                                a.get("lastName").getAsString(),
                                a.get("age").getAsInt()
                        )
                );
            }

            long id;
            try {
                id = data.get("Id").getAsLong();
            } catch (NullPointerException npe) {
                id = MovieServiceImpl.getIdCounter();
                MovieServiceImpl.incIdCounter();
            }
            return new Movie(
                    id,
                    data.get("Name").getAsString(),
                    data.get("Year of release").getAsInt(),
                    authors,
                    rental);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static long idFromJson(final HttpExchange httpExchange) {
        return Mapper.idFromJson(httpExchange);
    }
}
