package com.application_task.app.db.dao;

import com.application_task.app.db.dto.MovieDto;
import com.application_task.app.entity.Movie;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.exception.WrongRequestParamException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a movie dao, which has methods for accessing and manipulating movies in the database
 */
public interface MovieDao {
    /**
     * Creates movie with the related authors in the database if such doesn't exist
     *
     * @param movie movie to create in database
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    void create(Movie movie) throws DatabaseException;

    /**
     * Creates movie in the database if such doesn't exist
     *
     * @param movie movie to create in database
     * @return {@code id} of created movie or {@code -1} if such movie exists
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    long create(MovieDto movie) throws DatabaseException;

    /**
     * Retrieves a movie from the database if such exists
     *
     * @param id id of movie to retrieve
     * @return {@link Optional} of movie (empty optional if the movie doesn't exist)
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    Optional<Movie> get(long id) throws DatabaseException;

    /**
     * Retrieves a list of all movies from the database
     *
     * @return {@link List} of movies
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    List<Movie> getAll() throws DatabaseException;

    /**
     * Updates the given {@link Movie} in the database
     *
     * @param movie {@link Movie} to update in the database
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    void update(Movie movie) throws DatabaseException;

    /**
     * Deletes movie in the database by provided id
     *
     * @param id id of movie to delete in the database
     * @return {@link Optional} of deleted movie (empty optional if the movie doesn't exist)
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    Optional<Movie> delete(long id) throws DatabaseException;

    /**
     * Retrieves a list of movies which match the given filters
     *
     * @param filters {@link Map} of {@link String}s that contain filters to apply to search
     * @return {@link List} of movies that match the given filters
     * @throws WrongRequestParamException if given filters do not match possible filters
     * @throws DatabaseException          if database error occurs
     * @see DatabaseException
     * @see WrongRequestParamException
     */
    List<Movie> getWithFilters(Map<String, String> filters) throws WrongRequestParamException, DatabaseException;
}
