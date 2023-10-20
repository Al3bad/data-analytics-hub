package dev.alabbad.interfaces;

import dev.alabbad.exceptions.DatabaseConnectionException;

/**
 * The common interface for all data access objects
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public interface IDatabase<T> {
    public T getConnection();

    public T connect(String url) throws DatabaseConnectionException;
}
