package dev.alabbad.models;

import java.sql.SQLException;
import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.exceptions.UnauthorisedAction;

/**
 * The interface for data access objects
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public interface Dao<A, T> {
    public boolean createTable();

    public T get(A id) throws SQLException, EntityNotFoundException;

    public T insert(T entity) throws SQLException, EntityNotFoundException;

    public boolean delete(T entity, User loggedInUser) throws SQLException, UnauthorisedAction, EntityNotFoundException;
}
