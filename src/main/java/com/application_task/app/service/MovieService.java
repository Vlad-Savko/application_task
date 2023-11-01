package com.application_task.app.service;

import com.application_task.app.db.dto.MovieDto;
import com.application_task.app.entity.Movie;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.exception.WrongRequestParamException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MovieService {
    void create(Movie movie) throws DatabaseException;

    long create(MovieDto movie) throws DatabaseException;

    Optional<Movie> get(long id) throws DatabaseException;

    List<Movie> getAll() throws DatabaseException;

    void update(Movie movie) throws DatabaseException;

    Optional<Movie> delete(long id) throws DatabaseException;

    List<Movie> getWithFilters(Map<String, String> filters) throws WrongRequestParamException, DatabaseException;
}
