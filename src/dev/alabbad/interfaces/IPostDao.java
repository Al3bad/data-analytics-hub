package dev.alabbad.interfaces;

import java.sql.SQLException;
import java.util.ArrayList;

import dev.alabbad.exceptions.InvalidArgumentException;
import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.models.Post;
import dev.alabbad.models.User;

/**
 * The interface for Post data access object
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public interface IPostDao {
    public ArrayList<Post> getSome(String sortBy, String author, int limit)
                    throws InvalidArgumentException, SQLException;

    public Boolean deleteAll(User author, User loggedInUser) throws UnauthorisedAction, SQLException;

    public ArrayList<Integer> getSharesDistribution() throws SQLException;
}
