package com.application_task.app.service.impl;

import com.application_task.app.db.dao.RentalDao;
import com.application_task.app.db.dao.impl.RentalDaoImpl;
import com.application_task.app.entity.Rental;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.service.RentalService;

public class RentalServiceImpl implements RentalService {
    private final RentalDao dao;


    public RentalServiceImpl(String user, String password) {
        this.dao = new RentalDaoImpl(user, password);
    }
    public RentalServiceImpl(String user, String password, String databaseUrl) {
        this.dao = new RentalDaoImpl(user, password, databaseUrl);
    }

    @Override
    public long create(Rental rental) throws DatabaseException {
        return dao.create(rental);
    }

    @Override
    public void update(Rental rental, long movieId) throws DatabaseException {
        dao.update(rental, movieId);
    }
}
