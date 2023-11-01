package com.application_task.app.service;

import com.application_task.app.entity.Rental;
import com.application_task.app.exception.DatabaseException;

public interface RentalService {
    long create(Rental rental) throws DatabaseException;

    void update(Rental rental, long movieId) throws DatabaseException;
}
