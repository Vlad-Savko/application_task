package com.application_task.app.db.dao;

import com.application_task.app.util.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractDao {
    protected long getRentalId(Connection connection, String sqlCommandForGettingRentalId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sqlCommandForGettingRentalId);
        ResultSet rentalIdRs = ps.executeQuery();

        long rentalId = -1;

        while (rentalIdRs.next()) {
            rentalId = rentalIdRs.getLong(1);
        }
        if (rentalId == -1) {
            throw new SQLException(Constants.Db.DB_DATA_ERROR);
        }
        return rentalId;
    }
}
