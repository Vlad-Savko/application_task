package com.application_task.app.db.dao;

import com.application_task.app.util.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents an abstract dao, which has method for getting id of {@link com.application_task.app.entity.Rental}
 */
public abstract class AbstractDao {
    /**
     * The default implementation of getting id of rental from the database
     *
     * @param connection                   connection which is used for connecting to database
     * @param sqlCommandForGettingRentalId string with value of SQL command for getting rental id
     * @throws SQLException if database error occurs
     * @return {@code id} of rental or {@code -1} if rental doesn't exist
     * @see SQLException
     */
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
