package com.application_task.app.db.dao;

import com.application_task.app.db.dto.AuthorDto;
import com.application_task.app.entity.Author;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.exception.WrongRequestParamException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents an author dao, which has methods for accessing and manipulating authors in the database
 */
public interface AuthorDao {
    /**
     * Creates author in the database if such doesn't exist
     *
     * @param author author to create in database
     * @return {@code id} of new created author or {@code -1} if such author exists
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    long create(Author author) throws DatabaseException;

    /**
     * Retrieves an author from the database if such exists
     *
     * @param id id of author to retrieve
     * @return {@link Optional<Author>} of author (empty optional if the author doesn't exist)
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    Optional<Author> get(long id) throws DatabaseException;

    /**
     * Retrieves an author as dto from the database if such exists
     *
     * @param id id of author to retrieve as dto
     * @return {@link Optional<AuthorDto>} of author dto (empty optional if the author doesn't exist)
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    Optional<AuthorDto> getAsDto(long id) throws DatabaseException;

    /**
     * Retrieves a list of author dtos which match the given filters
     *
     * @param filters {@link Map} of {@link String}s that contain filters to apply to search
     * @return {@link List} of author dtos that match the given filters
     * @throws WrongRequestParamException if given filters do not match possible filters
     * @throws DatabaseException          if database error occurs
     * @see DatabaseException
     * @see WrongRequestParamException
     */
    List<AuthorDto> getWithFilters(Map<String, String> filters) throws WrongRequestParamException, DatabaseException;

    /**
     * Retrieves a list of all authors as dtos
     *
     * @return {@link List} of author dtos
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    List<AuthorDto> getAll() throws DatabaseException;

    /**
     * Updates the given {@link Author} in the database
     *
     * @param author {@link Author} to update in the database
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    void update(Author author) throws DatabaseException;

    /**
     * Updates the given {@link Author} with the related movies in the database
     *
     * @param authorDto {@link Author} to update in the database
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    void update(AuthorDto authorDto) throws DatabaseException;

    /**
     * Deletes author in the database by provided id
     *
     * @param id id of author to delete in the database
     * @return {@link Optional} of deleted author dto (empty optional if the author doesn't exist)
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    Optional<AuthorDto> delete(long id) throws DatabaseException;
}
