package com.application_task.app.db.dao;

import com.application_task.app.exception.DatabaseException;

import java.util.List;

/**
 * Represents a movie-author dao, which has methods for accessing and manipulating movies-authors references in the database
 */
public interface MovieAuthorDao {
    /**
     * Creates a reference between the movie by provided id and provided authors
     *
     * @param movieId id of movie to bind authors to
     * @param authors {@link List} of authors' ids to bind to movie
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    void createByMovieId(long movieId, List<Long> authors) throws DatabaseException;

    /**
     * Creates a reference between the author by provided id and provided movies
     *
     * @param authorId  id of author to bind movies to
     * @param moviesIds {@link List} of movies' ids to bind to author
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    void createByAuthorId(long authorId, List<Long> moviesIds) throws DatabaseException;

    /**
     * Truncates a reference between the movie by provided id and provided authors
     *
     * @param movieId    id of movie to unbind authors from
     * @param authorsIds {@link List} of authors' ids to unbind from movie
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    void unbindAuthorsFromMovie(long movieId, List<Long> authorsIds) throws DatabaseException;

    /**
     * Truncates a reference between the author by provided id and provided movies
     *
     * @param authorId  id of author to unbind movies from
     * @param moviesIds {@link List} of movies' ids to unbind from author
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    void unbindMoviesFromAuthor(long authorId, List<Long> moviesIds) throws DatabaseException;
}
