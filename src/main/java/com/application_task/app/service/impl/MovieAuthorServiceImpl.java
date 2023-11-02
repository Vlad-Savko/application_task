package com.application_task.app.service.impl;

import com.application_task.app.db.dao.MovieAuthorDao;
import com.application_task.app.db.dao.impl.MovieAuthorDaoImpl;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.service.MovieAuthorService;

import java.util.List;

public class MovieAuthorServiceImpl implements MovieAuthorService {
    private final MovieAuthorDao dao;

    public MovieAuthorServiceImpl(String user, String password) {
        this.dao = new MovieAuthorDaoImpl(user, password);
    }

    public MovieAuthorServiceImpl(String user, String password, String databaseUrl) {
        this.dao = new MovieAuthorDaoImpl(user, password, databaseUrl);
    }

    @Override
    public void bindMovieToAuthors(long movieId, List<Long> authorsIds) throws DatabaseException {
        dao.createByMovieId(movieId, authorsIds);
    }

    @Override
    public void bindAuthorToMovies(long authorId, List<Long> moviesIds) throws DatabaseException {
        dao.createByAuthorId(authorId, moviesIds);
    }

    @Override
    public void unbindAuthorsFromMovie(long movieId, List<Long> authorsIds) throws DatabaseException {
        dao.unbindAuthorsFromMovie(movieId, authorsIds);
    }

    @Override
    public void unbindMoviesFromAuthor(long authorId, List<Long> moviesIds) throws DatabaseException {
        dao.unbindMoviesFromAuthor(authorId, moviesIds);
    }
}
