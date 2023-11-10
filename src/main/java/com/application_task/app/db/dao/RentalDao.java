package com.application_task.app.db.dao;

import com.application_task.app.entity.Movie;
import com.application_task.app.entity.Rental;
import com.application_task.app.exception.DatabaseException;

/**
 * Represents a rental dao, which has methods for accessing and manipulating rentals in the database
 */
public interface RentalDao {
    /**
     * Creates rental in the database
     *
     * @param rental rental to create in database
     * @return {@code id} of created rental
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    long create(Rental rental) throws DatabaseException;

    /**
     * Updates the given {@link Rental} in the database and the given {@link Movie} by its id
     *
     * @param rental  {@link Rental} to update in the database
     * @param movieId {@code id} of movie to update related rental in the database
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    void update(Rental rental, long movieId) throws DatabaseException;
}
