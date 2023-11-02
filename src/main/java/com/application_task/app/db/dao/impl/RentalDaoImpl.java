package com.application_task.app.db.dao.impl;

import com.application_task.app.db.connection.ConnectionPool;
import com.application_task.app.db.connection.ConnectionPoolImpl;
import com.application_task.app.db.dao.AbstractDao;
import com.application_task.app.db.dao.RentalDao;
import com.application_task.app.entity.Rental;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.util.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RentalDaoImpl extends AbstractDao implements RentalDao {
    private final ConnectionPool connectionPool;

    private static final ThreadLocal<Long> idCounter = ThreadLocal.withInitial(() -> 100L);

    public RentalDaoImpl(String user, String password) {
        this.connectionPool = new ConnectionPoolImpl(user, password);
    }

    public RentalDaoImpl(String user, String password, String databaseUrl) {
        this.connectionPool = new ConnectionPoolImpl(user, password, databaseUrl);
    }

    @Override
    public long create(Rental rental) throws DatabaseException {
        long id = getIdCounter();

        String sqlCommand = Constants.Sql.Rental.INSERT_RENTAL.formatted(
                id,
                rental.start().toString(),
                rental.finish().toString());
        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sqlCommand);
            ps.execute();
        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }
        incIdCounter();
        return id;
    }

    @Override
    public void update(Rental rental, long movieId) throws DatabaseException {

        String sqlCommandForGettingRentalId = Constants.Sql.Rental.GET_RENTAL_ID_FOR_MOVIE.formatted(movieId);

        try (Connection connection = connectionPool.getConnection()) {
            long rentalId = getRentalId(connection, sqlCommandForGettingRentalId);
            PreparedStatement ps;

            String sqlCommandForUpdatingRental = Constants.Sql.Rental.UPDATE_RENTAL.formatted(
                    rental.start(),
                    rental.finish(),
                    rentalId
            );
            ps = connection.prepareStatement(sqlCommandForUpdatingRental);
            ps.execute();
        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }
    }

    public static void incIdCounter() {
        idCounter.set(idCounter.get() + 1);
    }

    public static long getIdCounter() {
        return idCounter.get();
    }
}
