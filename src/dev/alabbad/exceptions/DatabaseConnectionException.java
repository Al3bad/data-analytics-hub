package dev.alabbad.exceptions;

/**
 * Exception for failure of connecting to database
 */
public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException(String msg) {
        super(msg);
    }
}
