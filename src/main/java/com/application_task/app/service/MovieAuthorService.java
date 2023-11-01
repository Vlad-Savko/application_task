package com.application_task.app.service;

import com.application_task.app.exception.DatabaseException;

import java.util.List;

public interface MovieAuthorService {
    void bindMovieToAuthors(long movieId, List<Long> authorsIds) throws DatabaseException;

    void bindAuthorToMovies(long authorId, List<Long> moviesIds) throws DatabaseException;

    void unbindAuthorsFromMovie(long movieId, List<Long> authorsIds) throws DatabaseException;

    void unbindMoviesFromAuthor(long authorId, List<Long> moviesIds) throws DatabaseException;
}
