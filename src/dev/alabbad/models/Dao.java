package dev.alabbad.models;

import java.sql.SQLException;
import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.exceptions.UnauthorisedAction;

public interface Dao<A, T> {
    public boolean createTabel();

    public T get(A id) throws SQLException, EntityNotFoundException;

    public T insert(T entity) throws SQLException, EntityNotFoundException;

    public boolean delete(T entity, User loggedInUser) throws SQLException, UnauthorisedAction, EntityNotFoundException;
}
