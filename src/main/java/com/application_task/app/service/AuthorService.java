package com.application_task.app.service;

import com.application_task.app.db.dto.AuthorDto;
import com.application_task.app.entity.Author;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.exception.WrongRequestParamException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AuthorService {
    long create(Author author) throws DatabaseException;

    Optional<Author> get(long id) throws DatabaseException;

    Optional<AuthorDto> getAsDto(long id) throws DatabaseException;

    void update(Author author) throws DatabaseException;

    void update(AuthorDto authorDto) throws DatabaseException;

    Optional<AuthorDto> delete(long id) throws DatabaseException;

    List<AuthorDto> getAll() throws DatabaseException;

    List<AuthorDto> getWithFiltersAsDtos(Map<String, String> filters) throws WrongRequestParamException, DatabaseException;
}
