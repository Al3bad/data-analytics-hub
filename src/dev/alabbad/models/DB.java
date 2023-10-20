package dev.alabbad.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dev.alabbad.exceptions.DatabaseConnectionException;
import dev.alabbad.interfaces.IDatabase;

/**
 * This class holds a set of static methods to interact with the database
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class DB implements IDatabase<Connection> {
    public static final int SQLITE_CONSTRAINT = 19;
    private Connection connection;

    public Connection getConnection() {
        return this.connection;
    }

    /**
     * Connect to the database.
     *
     * @param sqliteFilename
     * @throws SQLException
     */
    @Override
    public Connection connect(String url) throws DatabaseConnectionException {
        try {
            // create a connection to the sqlite DB
            this.connection = DriverManager.getConnection(url);
            return this.connection;
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e.getMessage());
        }
    }
}
