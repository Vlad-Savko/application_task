package com.application_task.app.db.dao.impl;

import com.application_task.app.db.connection.ConnectionPool;
import com.application_task.app.db.connection.ConnectionPoolImpl;
import com.application_task.app.db.dao.AbstractDao;
import com.application_task.app.db.dto.MovieDto;
import com.application_task.app.entity.Rental;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.service.RentalService;
import com.application_task.app.service.impl.RentalServiceImpl;
import com.application_task.app.util.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a helper class for movie dao
 */
public class MovieDaoImplHelper extends AbstractDao {
    private final ConnectionPool connectionPool;
    private final RentalService rentalService;

    /**
     * Constructs a {@link MovieDaoImplHelper} with the needed services that will use datasource with the URL from properties file
     */
    public MovieDaoImplHelper(String user, String password) {
        this.rentalService = new RentalServiceImpl(user, password);
        this.connectionPool = new ConnectionPoolImpl(user, password);
    }

    /**
     * Constructs a {@link MovieDaoImplHelper} with the needed services that will use datasource with the provided credentials
     */
    public MovieDaoImplHelper(String user, String password, String databaseUrl) {
        this.rentalService = new RentalServiceImpl(user, password, databaseUrl);
        this.connectionPool = new ConnectionPoolImpl(user, password, databaseUrl);
    }

    /**
     * Deletes a movie by its id
     *
     * @param id id of movie to delete
     * @throws DatabaseException if database error occurs
     * @see MovieDaoImpl
     * @see DatabaseException
     */
    @SuppressWarnings("all")
    public void deleteMovie(long id) throws DatabaseException {

        String sqlCommandForGettingRentalId = Constants.Sql.Rental.GET_RENTAL_ID_FOR_MOVIE.formatted(id);
        try (Connection connection = connectionPool.getConnection()) {
            long rentalId = getRentalId(connection, sqlCommandForGettingRentalId);
            PreparedStatement ps;

            String sqlCommandForDeletingMovie =
                    Constants.Sql.Movie.DELETE.formatted(id);
            ps = connection.prepareStatement(sqlCommandForDeletingMovie);
            ps.execute();

            String sqlCommandForDeletingMovieRental =
                    Constants.Sql.Rental.DELETE.formatted(rentalId);
            ps = connection.prepareStatement(sqlCommandForDeletingMovieRental);
            ps.execute();
        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }
    }

    /**
     * Creates a movie in the database
     *
     * @param movie movie to create in the database
     * @return {@code id} of new created movie or {@code -1} if such movie exists
     * @throws DatabaseException if database error occurs
     * @see MovieDaoImpl
     * @see DatabaseException
     */
    public long createMovie(MovieDto movie) throws DatabaseException {
        if (this.get(movie.id()).isEmpty()) {
            long rentalId = rentalService.create(movie.rental());
            String sqlCommand = Constants.Sql.Movie.INSERT.formatted(
                    movie.id(),
                    movie.name(),
                    movie.yearOfRelease(),
                    rentalId
            );
            try (Connection connection = connectionPool.getConnection()) {
                PreparedStatement ps = connection.prepareStatement(sqlCommand);
                ps.execute();
            } catch (SQLException e) {
                throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
            }
            return movie.id();
        }
        return -1L;
    }

    /**
     * Retrieves the movie from the database by its id if such exists
     *
     * @param id id of movie to retrieve
     * @return {@link Optional} of movie if such exists (empty optional if no such movie in the database)
     * @throws DatabaseException if database error occurs
     * @see MovieDaoImpl
     * @see DatabaseException
     */
    private Optional<MovieDto> get(Long id) throws DatabaseException {
        String sqlCommandForGettingMovie = Constants.Sql.Movie.SELECT_ONE.formatted("ID", String.valueOf(id));
        String sqlCommandForGettingMovieRental = Constants.Sql.Rental.GET_RENTAL_FOR_MOVIE;

        MovieDto movie = null;

        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement moviePs = connection.prepareStatement(sqlCommandForGettingMovie);
            ResultSet movieResultSet = moviePs.executeQuery();


            while (movieResultSet.next()) {
                long rentalId = movieResultSet.getLong(4);
                PreparedStatement rentalPs = connection.prepareStatement(sqlCommandForGettingMovieRental.formatted(rentalId));
                ResultSet rentalResultSet = rentalPs.executeQuery();
                Rental rental = null;
                while (rentalResultSet.next()) {
                    rental = new Rental(
                            LocalDate.parse(rentalResultSet.getDate(1).toString()),
                            LocalDate.parse(rentalResultSet.getDate(2).toString())
                    );
                }
                movie = new MovieDto(
                        movieResultSet.getLong(1),
                        movieResultSet.getString(2),
                        movieResultSet.getInt(3),
                        rental
                );
            }
        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }
        return Optional.ofNullable(movie);
    }

    /**
     * Updates the movie in the database if such exists
     *
     * @param movie movie to update in the database
     * @throws DatabaseException if database error occurs
     * @see MovieDaoImpl
     * @see DatabaseException
     */
    public void updateMovie(MovieDto movie) throws DatabaseException {
        rentalService.update(movie.rental(), movie.id());
        String sqlCommand = Constants.Sql.Movie.UPDATE.formatted(movie.name(), movie.yearOfRelease(), movie.id());
        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sqlCommand);
            ps.execute();
        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }
    }
}
