package dev.alabbad.interfaces;

import java.sql.SQLException;
import java.util.ArrayList;

import dev.alabbad.exceptions.InvalidArgumentException;
import dev.alabbad.models.Post;

/**
 * The interface for Post data access object
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public interface IPostDao {
    public ArrayList<Post> getSome(String sortBy, String author, int limit)
                    throws InvalidArgumentException, SQLException;

    public Boolean deleteAll(String postAuthor) throws SQLException;

    public ArrayList<Integer> getSharesDistribution() throws SQLException;
}
