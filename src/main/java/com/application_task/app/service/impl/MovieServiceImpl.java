package com.application_task.app.service.impl;

import com.application_task.app.db.dao.MovieDao;
import com.application_task.app.db.dao.impl.MovieDaoImpl;
import com.application_task.app.db.dto.MovieDto;
import com.application_task.app.entity.Movie;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.exception.WrongRequestParamException;
import com.application_task.app.service.MovieService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * {@inheritDoc}
 */
public class MovieServiceImpl implements MovieService {
    private final MovieDao dao;
    private static final ThreadLocal<Long> idCounter = ThreadLocal.withInitial(() -> 1L);

    /**
     * Constructs a {@link MovieService} with the services that will use datasource with the URL from properties file
     */
    public MovieServiceImpl(String user, String password) {
        this.dao = new MovieDaoImpl(user, password);
    }

    /**
     * Constructs a {@link MovieService} with the services that will use datasource with provided credentials
     */
    public MovieServiceImpl(String user, String password, String databaseUrl) {
        this.dao = new MovieDaoImpl(user, password, databaseUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(Movie movie) throws DatabaseException {
        dao.create(movie);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long create(MovieDto movie) throws DatabaseException {
        return dao.create(movie);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Movie> get(long id) throws DatabaseException {
        return dao.get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Movie> getAll() throws DatabaseException {
        return dao.getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Movie movie) throws DatabaseException {
        dao.update(movie);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Movie> delete(long id) throws DatabaseException {
        return dao.delete(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Movie> getWithFilters(Map<String, String> filters) throws WrongRequestParamException, DatabaseException {
        return dao.getWithFilters(filters);
    }

    public static void incIdCounter() {
        idCounter.set(idCounter.get() + 1);
    }

    public static long getIdCounter() {
        return idCounter.get();
    }
}
