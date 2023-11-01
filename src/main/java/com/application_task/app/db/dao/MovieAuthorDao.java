package com.application_task.app.db.dao;

import com.application_task.app.exception.DatabaseException;

import java.util.List;

public interface MovieAuthorDao {
    void createByMovieId(long movieId, List<Long> authors) throws DatabaseException;

    void createByAuthorId(long authorId, List<Long> moviesIds) throws DatabaseException;

    void unbindAuthorsFromMovie(long movieId, List<Long> authorsIds) throws DatabaseException;

    void unbindMoviesFromAuthor(long authorId, List<Long> moviesIds) throws DatabaseException;

}
