package dev.alabbad.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.exceptions.UnauthorisedAction;

public class PostDao implements Dao<Integer, Post> {
    private Connection connection;

    public PostDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean createTabel() {
        try {
            // construct & execute query
            Statement stmt = connection.createStatement();
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS post (
                        id INTEGER NOT NULL UNIQUE PRIMARY KEY,
                        content TEXT NOT NULL,
                        author TEXT NOT NULL,
                        likes INTEGER NOT NULL,
                        shares INTEGER NOT NULL,
                        dateTime STRING NOT NULL
                    );
                    """);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Post get(Integer id) throws SQLException, EntityNotFoundException {
        // construct & execute query
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM post WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            // extract data from result
            String content = rs.getString("content");
            String author = rs.getString("author");
            int likes = rs.getInt("likes");
            int shares = rs.getInt("shares");
            String dateTime = rs.getString("dateTime");
            return new Post(id, content, author, likes, shares, dateTime);
        }
        throw new EntityNotFoundException("[ERROR-DB] Post not found!");
    }

    @Override
    public Post insert(Post post) throws SQLException, EntityNotFoundException {
        // construct & execute query
        PreparedStatement stmt = connection.prepareStatement("""
                INSERT INTO post (id,content, author, likes, shares, dateTime)
                VALUES (?, ?, ?, ?, ?, ?);
                """);
        if (post.getID() == null) {
            // auto generate id
            stmt.setNull(1, Types.NULL);
        } else {
            // use the provided id
            stmt.setInt(1, post.getID());
        }
        stmt.setString(2, post.getContent());
        stmt.setString(3, post.getAuthor());
        stmt.setInt(4, post.getLikes());
        stmt.setInt(5, post.getShares());
        stmt.setString(6, post.getDateTime());
        stmt.executeUpdate();
        return new Post(getLastInsertedRowID(), post.getContent(), post.getAuthor(), post.getLikes(), post.getShares(),
                post.getDateTime());
    }

    @Override
    public boolean delete(Post post, User loggedInUser)
            throws SQLException, UnauthorisedAction, EntityNotFoundException {
        // check for authorisation for this operation
        Boolean isAdmin = loggedInUser instanceof AdminUser;
        if (!isAdmin && !post.getAuthor().toLowerCase().equals(loggedInUser.getUsername().toLowerCase())) {
            throw new UnauthorisedAction("You're unauthorised to delete a posts of someone else's post!");
        }
        // construct & execute query
        String stmtStr = "DELETE FROM post WHERE id = ?";
        if (!(isAdmin)) {
            stmtStr += "  AND LOWER(author) = LOWER(?)";

        }
        PreparedStatement stmt = connection.prepareStatement(stmtStr);
        stmt.setInt(1, post.getID());
        stmt.setString(2, post.getAuthor());
        stmt.executeUpdate();
        return true;
    }

    public Boolean deleteAll(String postAuthor) throws SQLException {
        // construct & execute query
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM post WHERE LOWER(author) = LOWER(?)");
        stmt.setString(1, postAuthor);
        stmt.executeUpdate();
        return true;
    }

    /**
     * Get the ID of the last inserted row in the database
     *
     * @return ID number
     * @throws SQLException when no ID is found
     */
    private int getLastInsertedRowID() throws SQLException {
        ResultSet rs = connection.createStatement().executeQuery("SELECT last_insert_rowid() as id");
        if (!rs.next()) {
            throw new SQLException("Last inserted ID is not found!");
        }
        return rs.getInt("id");
    }
}
