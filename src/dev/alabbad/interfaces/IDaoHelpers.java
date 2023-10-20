package dev.alabbad.interfaces;

import java.sql.SQLException;

/**
 * The interface is for data access object helpers
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public interface IDaoHelpers {
    public int getLastInsertedRowID() throws SQLException;
}
