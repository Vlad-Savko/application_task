package com.application_task.app.service.impl;

import com.application_task.app.db.dao.AuthorDao;
import com.application_task.app.db.dao.impl.AuthorDaoImpl;
import com.application_task.app.db.dto.AuthorDto;
import com.application_task.app.entity.Author;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.exception.WrongRequestParamException;
import com.application_task.app.service.AuthorService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * {@inheritDoc}
 */
public class AuthorServiceImpl implements AuthorService {
    private final AuthorDao dao;
    private static final ThreadLocal<Long> idCounter = ThreadLocal.withInitial(() -> 1L);

    /**
     * Constructs an {@link AuthorService} with the services that will use datasource with the URL from properties file
     */
    public AuthorServiceImpl(String user, String password) {
        this.dao = new AuthorDaoImpl(user, password);
    }

    /**
     * Constructs an {@link AuthorService} with the services that will use datasource with provided credentials
     */
    public AuthorServiceImpl(String user, String password, String databaseUrl) {
        this.dao = new AuthorDaoImpl(user, password, databaseUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long create(Author author) throws DatabaseException {
        return dao.create(author);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Author> get(long id) throws DatabaseException {
        return dao.get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AuthorDto> getAsDto(long id) throws DatabaseException {
        return dao.getAsDto(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Author author) throws DatabaseException {
        dao.update(author);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(AuthorDto authorDto) throws DatabaseException {
        dao.update(authorDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AuthorDto> delete(long id) throws DatabaseException {
        return dao.delete(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AuthorDto> getAll() throws DatabaseException {
        return dao.getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AuthorDto> getWithFiltersAsDtos(Map<String, String> filters) throws WrongRequestParamException, DatabaseException {
        return dao.getWithFilters(filters);
    }

    public static void incIdCounter() {
        idCounter.set(idCounter.get() + 1);
    }

    public static long getIdCounter() {
        return idCounter.get();
    }
}
