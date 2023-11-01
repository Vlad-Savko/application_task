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

public class MovieAuthorDaoImpl implements MovieAuthorDao {
    private final ConnectionPool connectionPool;

    public MovieAuthorDaoImpl(String user, String password) {
        connectionPool = new ConnectionPoolImpl(user, password);
    }

    @Override
    public void createByMovieId(long movieId, List<Long> authors) throws DatabaseException {
        modifyMovieAuthors(authors, Constants.Sql.MovieAuthors.INSERT, movieId);
    }

    @Override
    public void createByAuthorId(long authorId, List<Long> moviesIds) throws DatabaseException {
        modifyAuthorMovies(moviesIds, Constants.Sql.MovieAuthors.INSERT, authorId);
    }

    @Override
    public void unbindAuthorsFromMovie(long movieId, List<Long> authorsIds) throws DatabaseException {
        modifyMovieAuthors(authorsIds, Constants.Sql.MovieAuthors.DELETE, movieId);
    }

    @Override
    public void unbindMoviesFromAuthor(long authorId, List<Long> moviesIds) throws DatabaseException {
        modifyAuthorMovies(moviesIds, Constants.Sql.MovieAuthors.DELETE, authorId);
    }

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
