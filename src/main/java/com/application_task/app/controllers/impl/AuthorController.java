package com.application_task.app.controllers.impl;

import com.application_task.app.controllers.WebController;
import com.application_task.app.db.dto.AuthorDto;
import com.application_task.app.db.dto.MovieDto;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.exception.WrongRequestParamException;
import com.application_task.app.exception.WrongSearchFiltersException;
import com.application_task.app.service.AuthorService;
import com.application_task.app.service.MovieAuthorService;
import com.application_task.app.service.MovieService;
import com.application_task.app.service.impl.AuthorServiceImpl;
import com.application_task.app.service.impl.MovieAuthorServiceImpl;
import com.application_task.app.service.impl.MovieServiceImpl;
import com.application_task.app.util.Constants;
import com.application_task.app.util.PropertiesLoader;
import com.application_task.app.util.StringParser;
import com.application_task.app.util.mappers.AuthorMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.application_task.app.util.Constants.HttpMethod.*;
import static com.application_task.app.util.StringParser.parseMovieFilters;

public class AuthorController extends WebController {
    protected final AuthorService authorService;
    protected final MovieService movieService;
    protected final MovieAuthorService movieAuthorService;

    public AuthorController() {
        String user = PropertiesLoader.getProperty(Constants.USER_PROPERTY_KEY);
        String password = PropertiesLoader.getProperty(Constants.PASSWORD_PROPERTY_KEY);
        authorService = new AuthorServiceImpl(user, password);
        movieAuthorService = new MovieAuthorServiceImpl(user, password);
        movieService = new MovieServiceImpl(user, password);
    }

    public AuthorController(String user, String password, String databaseUrl) {
        authorService = new AuthorServiceImpl(user, password, databaseUrl);
        movieAuthorService = new MovieAuthorServiceImpl(user, password, databaseUrl);
        movieService = new MovieServiceImpl(user, password, databaseUrl);
    }

    @Override
    public void execute(HttpExchange httpExchange) {
        String request = httpExchange.getRequestURI().getPath();

        String requestString = request.substring(8);
        switch (httpExchange.getRequestMethod()) {
            case GET -> {
                if (requestString.isEmpty()) {
                    try {
                        List<AuthorDto> authors = authorService.getAll();
                        writeResponse(httpExchange, authors.stream(), Constants.HttpStatus.OK);
                    } catch (DatabaseException ex) {
                        errorResponse(httpExchange, ex.getMessage(), Constants.HttpStatus.NOT_FOUND);
                    } catch (IOException e) {
                        throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                    }
                } else {
                    String ids = requestString.substring(1);
                    if (ids.matches(Constants.OfServer.ITEMS_SEPARATED_BY_COMMA_REG_EXP)) {
                        try {
                            List<Long> authorsIds = Arrays.stream(ids.split(","))
                                    .map(Long::parseLong)
                                    .toList();
                            List<AuthorDto> authors = new ArrayList<>();
                            try {
                                for (Long id : authorsIds) {
                                    authors.add(authorService.getAsDto(id).orElseThrow(
                                            () -> new NoSuchElementException(Constants.Message.ERROR_NO_SUCH_AUTHOR)
                                    ));
                                }
                            } catch (NoSuchElementException nsee) {
                                writeErrorResponse(httpExchange, nsee.getMessage(), Constants.HttpStatus.NOT_FOUND);
                            }
                            writeResponse(httpExchange, authors.stream(), Constants.HttpStatus.OK);
                        } catch (IOException e) {
                            throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                        }
                    } else if (requestString.substring(1).equals("search")) {
                        try {
                            String searchFilters = httpExchange.getRequestURI().toString().substring(16);
                            List<AuthorDto> authors = new ArrayList<>();
                            try {
                                authors = authorService.getWithFiltersAsDtos(parseMovieFilters(searchFilters, StringParser.Type.AUTHOR));
                            } catch (WrongSearchFiltersException e) {
                                writeErrorResponse(httpExchange, e.getMessage(), Constants.HttpStatus.NOT_FOUND);
                            } catch (WrongRequestParamException e) {
                                writeErrorResponse(httpExchange, Constants.Message.ERROR_WRONG_REQUEST_PARAM, Constants.HttpStatus.NOT_FOUND);
                            }
                            writeResponse(httpExchange, authors.stream(), Constants.HttpStatus.OK);
                        } catch (IOException e) {
                            throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                        }
                    } else {
                        try {
                            AuthorDto author = null;
                            String id = "";
                            try {
                                id = request.substring(9);
                                author = authorService.getAsDto(Long.parseLong(id))
                                        .orElseThrow(
                                                () -> new NoSuchElementException(Constants.Message.ERROR_NO_SUCH_AUTHOR)
                                        );
                            } catch (NoSuchElementException | NumberFormatException e) {
                                writeErrorResponse(
                                        httpExchange,
                                        String.format("%s: %s", Constants.Message.ERROR_NO_SUCH_AUTHOR, id),
                                        Constants.HttpStatus.NOT_FOUND);
                            }
                            writeResponse(httpExchange, Stream.of(author).filter(Objects::nonNull), Constants.HttpStatus.OK);
                        } catch (IOException e) {
                            throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                        }
                    }
                }
            }
            case POST -> {
                if (requestString.isEmpty()) {
                    Optional<AuthorDto> authorOptional = AuthorMapper.fromJson(httpExchange);
                    if (authorOptional.isPresent()) {
                        try {
                            AuthorDto authorDto = authorOptional.get();
                            List<MovieDto> relatedMovies = authorDto.getRelatedMovies();
                            List<Long> moviesIds = new ArrayList<>();

                            for (MovieDto movie : relatedMovies) {
                                long result = movieService.create(movie);
                                if (result != -1) {
                                    moviesIds.add(result);
                                }
                            }
                            long authorId;
                            if (authorService.getAsDto(authorDto.id()).isEmpty()) {
                                authorId = authorService.create(authorDto.getAuthor());
                                if (!moviesIds.isEmpty()) {
                                    movieAuthorService.bindAuthorToMovies(authorId, moviesIds);
                                    movieAuthorService.bindAuthorToMovies(
                                            authorId,
                                            relatedMovies.stream()
                                                    .map(MovieDto::id)
                                                    .filter(id -> !moviesIds.contains(id))
                                                    .toList());
                                } else {
                                    movieAuthorService.bindAuthorToMovies(authorId,
                                            relatedMovies.stream()
                                                    .map(MovieDto::id)
                                                    .toList());
                                }
                            }
                            try {
                                writeResponse(httpExchange, Stream.of(authorDto), Constants.HttpStatus.CREATED);
                            } catch (IOException e) {
                                throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                            }
                        } catch (DatabaseException ex) {
                            errorResponse(httpExchange, ex.getMessage(), Constants.HttpStatus.NOT_FOUND);
                        }
                    } else {
                        errorResponse(httpExchange, Constants.Message.ERROR_WRONG_REQUEST_BODY, Constants.HttpStatus.WRONG_JSON_BODY);
                    }
                }
            }
            case PUT -> {
                if (requestString.isEmpty()) {
                    Optional<AuthorDto> authorOptional = AuthorMapper.fromJson(httpExchange);
                    if (authorOptional.isPresent()) {
                        AuthorDto author = authorOptional.get();
                        try {
                            authorService.update(author);
                            try {
                                writeResponse(httpExchange, Stream.of(author), Constants.HttpStatus.CREATED);
                            } catch (IOException e) {
                                throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                            }
                        } catch (DatabaseException ex) {
                            errorResponse(httpExchange, ex.getMessage(), Constants.HttpStatus.NOT_FOUND);
                        }
                    } else {
                        errorResponse(httpExchange, Constants.Message.ERROR_WRONG_REQUEST_BODY, Constants.HttpStatus.WRONG_JSON_BODY);
                    }
                }
            }
            case DELETE -> {
                if (requestString.isEmpty()) {
                    String fullUrl = httpExchange.getRequestURI().toString();
                    if (fullUrl.length() > 8) {
                        String idsToDelete = fullUrl.substring(12);
                        if (idsToDelete.matches(Constants.OfServer.ITEMS_SEPARATED_BY_COMMA_REG_EXP)) {
                            try {
                                List<Long> authorsIds = Arrays.stream(idsToDelete.split(","))
                                        .map(Long::parseLong)
                                        .toList();
                                List<AuthorDto> deletedAuthors = new ArrayList<>();
                                for (Long id : authorsIds) {
                                    Optional<AuthorDto> authorOptional = authorService.delete(id);
                                    authorOptional.ifPresentOrElse(deletedAuthors::add,
                                            () -> {
                                                throw new NoSuchElementException(Constants.Message.ERROR_NO_SUCH_MOVIE);
                                            });
                                }
                                writeResponse(httpExchange, deletedAuthors.stream(), Constants.HttpStatus.OK);
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
                            deleteAuthor(httpExchange, id);
                        }
                    } else {
                        deleteAuthor(httpExchange, AuthorMapper.idFromJson(httpExchange));
                    }
                }
            }
        }
    }

    private void writeResponse(HttpExchange httpExchange, Stream<AuthorDto> authors, int status) throws IOException {
        writeResponse(httpExchange,
                authors
                        .map(AuthorMapper::toJson)
                        .collect(Collectors.toList()),
                status);
    }

    private void deleteAuthor(HttpExchange httpExchange, long id) {
        if (id > 0) {
            try {
                authorService.delete(id).ifPresentOrElse(author -> {
                            try {
                                writeResponse(httpExchange, Stream.of(author), Constants.HttpStatus.DELETED);
                            } catch (IOException e) {
                                throw new RuntimeException(Constants.Message.ERROR_SENDING_RESPONSE, e);
                            }
                        },
                        () -> errorResponse(httpExchange, String.format("%s: %s", Constants.Message.ERROR_NO_SUCH_AUTHOR, id), Constants.HttpStatus.NOT_FOUND)
                );
            } catch (DatabaseException ex) {
                errorResponse(httpExchange, ex.getMessage(), Constants.HttpStatus.NOT_FOUND);
            }
        } else {
            errorResponse(httpExchange, Constants.Message.ERROR_WRONG_REQUEST_BODY, Constants.HttpStatus.WRONG_JSON_BODY);
        }
    }
}
