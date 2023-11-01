package com.application_task.app.controllers.impl;

import com.application_task.app.controllers.WebController;
import com.application_task.app.entity.Movie;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.exception.WrongSearchFiltersException;
import com.application_task.app.service.MovieService;
import com.application_task.app.service.impl.MovieServiceImpl;
import com.application_task.app.util.Constants;
import com.application_task.app.util.PropertiesLoader;
import com.application_task.app.util.StringParser;
import com.application_task.app.util.mappers.MovieMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.application_task.app.util.Constants.HttpMethod.*;
import static com.application_task.app.util.StringParser.parseMovieFilters;

public class MovieController extends WebController {
    private final MovieService movieService;

    public MovieController() {
        String user = PropertiesLoader.getProperty(Constants.USER_PROPERTY_KEY);
        String password = PropertiesLoader.getProperty(Constants.PASSWORD_PROPERTY_KEY);
        this.movieService = new MovieServiceImpl(user, password);
    }

    @Override
    public void execute(HttpExchange httpExchange) {
        String request = httpExchange.getRequestURI().getPath();

        String requestString = request.substring(7);
        switch (httpExchange.getRequestMethod()) {
            case GET -> {
                if (requestString.isEmpty()) {
                    try {
                        List<Movie> movies = movieService.getAll();
                        writeResponse(httpExchange, movies.stream(), Constants.HttpStatus.OK);
                    } catch (DatabaseException ex) {
                        errorResponse(httpExchange, ex.getMessage(), Constants.HttpStatus.NOT_FOUND);
                    } catch (IOException e) {
                        throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                    }
                } else {
                    String ids = requestString.substring(1);
                    if (ids.matches(Constants.OfServer.ITEMS_SEPARATED_BY_COMMA_REG_EXP)) {
                        try {
                            List<Long> moviesIds = new ArrayList<>();
                            for (String id : ids.split(",")) {
                                try {
                                    moviesIds.add(Long.parseLong(id));
                                } catch (NumberFormatException nfe) {
                                    writeErrorResponse(httpExchange,
                                            String.format("%s: %s", Constants.Message.ERROR_NO_SUCH_MOVIE, id), Constants.HttpStatus.NOT_FOUND);
                                    return;
                                }
                            }
                            List<Movie> movies = new ArrayList<>();
                            try {
                                for (Long id : moviesIds) {
                                    movies.add(movieService.get(id).orElseThrow(
                                            () -> new NoSuchElementException(Constants.Message.ERROR_NO_SUCH_MOVIE)
                                    ));
                                }
                            } catch (NoSuchElementException nsee) {
                                writeErrorResponse(httpExchange, nsee.getMessage(), Constants.HttpStatus.NOT_FOUND);
                            }
                            writeResponse(httpExchange, movies.stream(), Constants.HttpStatus.OK);

                        } catch (IOException e) {
                            throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                        }
                    } else if (requestString.substring(1).equals("search")) {
                        try {
                            String searchFilters = httpExchange.getRequestURI().toString().substring(15);
                            List<Movie> movies = new ArrayList<>();
                            try {
                                movies = movieService.getWithFilters(parseMovieFilters(searchFilters, StringParser.Type.MOVIE));
                            } catch (WrongSearchFiltersException e) {
                                writeErrorResponse(httpExchange, e.getMessage(), Constants.HttpStatus.NOT_FOUND);
                            }
                            writeResponse(httpExchange, movies.stream(), Constants.HttpStatus.OK);
                        } catch (IOException e) {
                            try {
                                writeErrorResponse(httpExchange, Constants.Message.ERROR_WRONG_REQUEST_PARAM, Constants.HttpStatus.NOT_FOUND);
                            } catch (IOException ex) {
                                throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                            }
                        }
                    } else {
                        try {
                            Movie movie = null;
                            String id = "";
                            try {
                                id = request.substring(8);
                                String finalId = id;
                                movie = movieService.get(Long.parseLong(id))
                                        .orElseThrow(
                                                () -> new NoSuchElementException(String.format("%s: %s", Constants.Message.ERROR_NO_SUCH_MOVIE, finalId))
                                        );
                            } catch (NoSuchElementException nsee) {
                                writeErrorResponse(httpExchange, nsee.getMessage(), Constants.HttpStatus.NOT_FOUND);
                            } catch (NumberFormatException nfe) {
                                writeErrorResponse(httpExchange,
                                        String.format("%s: %s", Constants.Message.ERROR_NO_SUCH_MOVIE, id),
                                        Constants.HttpStatus.NOT_FOUND);
                            }
                            writeResponse(httpExchange, Stream.of(movie).filter(Objects::nonNull), Constants.HttpStatus.OK);
                        } catch (IOException e) {
                            try {
                                writeErrorResponse(httpExchange, Constants.Message.ERROR_NO_SUCH_MOVIE, Constants.HttpStatus.NOT_FOUND);
                            } catch (IOException ex) {
                                throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                            }
                        }
                    }
                }
            }
            case POST -> {
                if (requestString.isEmpty()) {
                    Optional<Movie> movieOptional = MovieMapper.fromJson(httpExchange);
                    if (movieOptional.isPresent()) {
                        Movie movie = movieOptional.get();
                        try {
                            movieService.create(movie);
                        } catch (DatabaseException e) {
                            try {
                                writeErrorResponse(httpExchange, Constants.Message.DATABASE_ERROR, Constants.HttpStatus.SERVER_INTERNAL_ERROR);
                            } catch (IOException ex) {
                                throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                            }
                        }
                        try {
                            writeResponse(httpExchange, Stream.of(movie), Constants.HttpStatus.CREATED);
                        } catch (IOException e) {
                            throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                        }
                    } else {
                        errorResponse(httpExchange, Constants.Message.ERROR_WRONG_REQUEST_BODY, Constants.HttpStatus.WRONG_JSON_BODY);
                    }
                }
            }
            case DELETE -> {
                if (requestString.isEmpty()) {
                    String fullUrl = httpExchange.getRequestURI().toString();
                    if (fullUrl.length() > 7) {
                        String idsToDelete = "";
                        try {
                            idsToDelete = fullUrl.substring(11);
                        } catch (IndexOutOfBoundsException ioobe) {
                            errorResponse(httpExchange, Constants.Message.ERROR_WRONG_REQUEST_PARAM, Constants.HttpStatus.WRONG_JSON_BODY);
                        }
                        if (idsToDelete.matches(Constants.OfServer.ITEMS_SEPARATED_BY_COMMA_REG_EXP)) {
                            try {
                                List<Long> moviesIds = Arrays.stream(idsToDelete.split(","))
                                        .map(Long::parseLong)
                                        .toList();
                                List<Movie> deletedMovies = new ArrayList<>();
                                for (Long id : moviesIds) {
                                    Optional<Movie> movieOptional = movieService.delete(id);
                                    movieOptional.ifPresentOrElse(deletedMovies::add,
                                            () -> {
                                                throw new NoSuchElementException(Constants.Message.ERROR_NO_SUCH_MOVIE);
                                            });
                                }
                                writeResponse(httpExchange, deletedMovies.stream(), Constants.HttpStatus.OK);
                            } catch (IOException e) {
                                throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                            }
                        } else {
                            long id = -1;
                            try {
                                id = Long.parseLong(idsToDelete);
                            } catch (NumberFormatException nfe) {
                                errorResponse(httpExchange, Constants.Message.ERROR_WRONG_REQUEST_PARAM, Constants.HttpStatus.WRONG_JSON_BODY);
                            }
                            deleteMovie(httpExchange, id);
                        }
                    } else {
                        deleteMovie(httpExchange, MovieMapper.idFromJson(httpExchange));
                    }
                }
            }
            case PUT -> {
                if (requestString.isEmpty()) {
                    Optional<Movie> movieOptional = MovieMapper.fromJson(httpExchange);
                    if (movieOptional.isPresent()) {
                        Movie movie = movieOptional.get();
                        try {
                            movieService.update(movie);
                            writeResponse(httpExchange, Stream.of(movie), Constants.HttpStatus.CREATED);
                        } catch (DatabaseException ex) {
                            errorResponse(httpExchange, ex.getMessage(), Constants.HttpStatus.NOT_FOUND);
                        } catch (IOException e) {
                            throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                        }
                    } else {
                        errorResponse(httpExchange, Constants.Message.ERROR_WRONG_REQUEST_BODY, Constants.HttpStatus.WRONG_JSON_BODY);
                    }
                }
            }
        }
    }

    private void writeResponse(HttpExchange httpExchange, Stream<Movie> movies, int status) throws IOException {
        writeResponse(httpExchange,
                movies
                        .map(MovieMapper::toJson)
                        .collect(Collectors.toList()),
                status);
    }


    private void deleteMovie(HttpExchange httpExchange, long movieId) {
        if (movieId > 0) {
            try {
                movieService.delete(movieId).ifPresentOrElse(movie -> {
                            try {
                                writeResponse(httpExchange, Stream.of(movie), Constants.HttpStatus.DELETED);

                            } catch (IOException e) {
                                throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                            }
                        },
                        () -> errorResponse(httpExchange, String.format("%s: %s", Constants.Message.ERROR_NO_SUCH_MOVIE, movieId), Constants.HttpStatus.NOT_FOUND));
            } catch (DatabaseException ex) {
                errorResponse(httpExchange, ex.getMessage(), Constants.HttpStatus.NOT_FOUND);
            }
        } else {
            errorResponse(httpExchange, Constants.Message.ERROR_NO_SUCH_MOVIE, Constants.HttpStatus.NOT_FOUND);
        }
    }
}
