package dev.alabbad.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;

import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.models.User;

/**
 * The interface for User data access object
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public interface IUserDao {
    public HashMap<String, User> getAll() throws SQLException;

    public User update(User updatedUser, String username, String password)
                    throws EntityNotFoundException, SQLException, UnauthorisedAction;

    public User upgrade(User user) throws EntityNotFoundException, SQLException;

    public User login(String username, String password)
                    throws EntityNotFoundException, SQLException, UnauthorisedAction;

    public User updateProfileImg(String username, InputStream profileImg)
                    throws SQLException, EntityNotFoundException, IOException;
}
