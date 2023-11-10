package com.application_task.app.db.dao.impl;

import com.application_task.app.db.connection.ConnectionPool;
import com.application_task.app.db.connection.ConnectionPoolImpl;
import com.application_task.app.db.dao.MovieAuthorDao;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.util.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * {@inheritDoc}
 */
public class MovieAuthorDaoImpl implements MovieAuthorDao {
    private final ConnectionPool connectionPool;

    /**
     * Constructs a {@link MovieAuthorDao} with the needed services that will use datasource with the URL from properties file
     */
    public MovieAuthorDaoImpl(String user, String password) {
        connectionPool = new ConnectionPoolImpl(user, password);
    }

    /**
     * Constructs a {@link MovieAuthorDao} with the needed services that will use datasource with the provided credentials
     */
    public MovieAuthorDaoImpl(String user, String password, String databaseUrl) {
        connectionPool = new ConnectionPoolImpl(user, password, databaseUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createByMovieId(long movieId, List<Long> authors) throws DatabaseException {
        modifyMovieAuthors(authors, Constants.Sql.MovieAuthors.INSERT, movieId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createByAuthorId(long authorId, List<Long> moviesIds) throws DatabaseException {
        modifyAuthorMovies(moviesIds, Constants.Sql.MovieAuthors.INSERT, authorId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindAuthorsFromMovie(long movieId, List<Long> authorsIds) throws DatabaseException {
        modifyMovieAuthors(authorsIds, Constants.Sql.MovieAuthors.DELETE, movieId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindMoviesFromAuthor(long authorId, List<Long> moviesIds) throws DatabaseException {
        modifyAuthorMovies(moviesIds, Constants.Sql.MovieAuthors.DELETE, authorId);
    }

    /**
     * Modifies movies by provided ids by binding provided author to them
     *
     * @param moviesIds  ids of movies to bind author to
     * @param sqlCommand {@link String} which has the value of SQL command for modifying needed movies and author
     * @param authorId   id of author to bind to movies
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    @SuppressWarnings("all")
    private void modifyAuthorMovies(List<Long> moviesIds, String sqlCommand, long authorId) throws DatabaseException {
        try (Connection connection = connectionPool.getConnection()) {
            for (Long movieId : moviesIds) {
                String tempSqlCommand = sqlCommand.formatted(movieId, authorId);
                PreparedStatement ps = connection.prepareStatement(tempSqlCommand);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }
    }

    /**
     * Modifies authors by provided ids by binding provided movie to them
     *
     * @param authors    ids of authors to bind movie to
     * @param sqlCommand {@link String} which has the value of SQL command for modifying needed authors and movie
     * @param movieId    id of movie to bind to authors
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    @SuppressWarnings("all")
    private void modifyMovieAuthors(List<Long> authors, String sqlCommand, long movieId) throws DatabaseException {
        try (Connection connection = connectionPool.getConnection()) {
            for (Long authorId : authors) {
                String tempSqlCommand = sqlCommand.formatted(movieId, authorId);
                PreparedStatement ps = connection.prepareStatement(tempSqlCommand);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }
    }
}
