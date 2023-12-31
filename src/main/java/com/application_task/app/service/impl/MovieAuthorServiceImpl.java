package com.application_task.app.service.impl;

import com.application_task.app.db.dao.MovieAuthorDao;
import com.application_task.app.db.dao.impl.MovieAuthorDaoImpl;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.service.MovieAuthorService;

import java.util.List;

/**
 * {@inheritDoc}
 */
public class MovieAuthorServiceImpl implements MovieAuthorService {
    private final MovieAuthorDao dao;

    /**
     * Constructs a {@link MovieAuthorService} with the services that will use datasource with the URL from properties file
     */
    public MovieAuthorServiceImpl(String user, String password) {
        this.dao = new MovieAuthorDaoImpl(user, password);
    }

    /**
     * Constructs a {@link MovieAuthorService} with the services that will use datasource with provided credentials
     */
    public MovieAuthorServiceImpl(String user, String password, String databaseUrl) {
        this.dao = new MovieAuthorDaoImpl(user, password, databaseUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindMovieToAuthors(long movieId, List<Long> authorsIds) throws DatabaseException {
        dao.createByMovieId(movieId, authorsIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindAuthorToMovies(long authorId, List<Long> moviesIds) throws DatabaseException {
        dao.createByAuthorId(authorId, moviesIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindAuthorsFromMovie(long movieId, List<Long> authorsIds) throws DatabaseException {
        dao.unbindAuthorsFromMovie(movieId, authorsIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindMoviesFromAuthor(long authorId, List<Long> moviesIds) throws DatabaseException {
        dao.unbindMoviesFromAuthor(authorId, moviesIds);
    }
}
