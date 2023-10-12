package dev.alabbad.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class holds a set of static methods to interact with the database
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class DB {
    public static final int SQLITE_CONSTRAINT = 19;
    private static Connection conn;

    public static Connection getConnection() {
        return conn;
    }

    /**
     * Connect to the database.
     *
     * @param sqliteFilename
     * @throws SQLException
     */
    public static Connection connect(String sqliteFilename) throws SQLException {
        String url = "jdbc:sqlite:" + sqliteFilename;
        // create a connection to the sqlite DB
        conn = DriverManager.getConnection(url);
        return conn;
    }
}
