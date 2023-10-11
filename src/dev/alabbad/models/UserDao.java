package dev.alabbad.models;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.exceptions.EntityNotFoundException;

public class UserDao implements Dao<String, User> {
    private Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean createTabel() {
        try {
            // construct & execute query
            Statement stmt = connection.createStatement();
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS user (
                        username TEXT UNIQUE NOT NULL PRIMARY KEY CHECK(TRIM(username) != ''),
                        password TEXT NOT NULL CHECK(LENGTH(password) > 3),
                        fname TEXT NOT NULL CHECK(TRIM(fname) != ''),
                        lname TEXT NOT NULL CHECK(TRIM(lname) != ''),
                        isAdmin INTEGER NOT NULL DEFAULT 0,
                        isVIP INTEGER NOT NULL DEFAULT 0,
                        profileImg BLOB DEFAULT NULL
                    );
                    """);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public User get(String username) throws SQLException, EntityNotFoundException {
        // construct & execute query
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT username, fname, lname, isVIP, isAdmin, profileImg FROM user WHERE username = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            // extract data from result
            String user = rs.getString("username");
            String fname = rs.getString("fname");
            String lname = rs.getString("lname");
            Boolean isVIP = rs.getBoolean("isVIP");
            Boolean isAdmin = rs.getBoolean("isAdmin");
            InputStream profileImg = rs.getBinaryStream("profileImg");
            // create the correct user object based on the extracted flags
            if (isAdmin) {
                return new AdminUser(user, fname, lname, profileImg);
            } else if (isVIP) {
                return new VIPUser(user, fname, lname, profileImg);
            }
            return new User(user, fname, lname, profileImg);
        }
        throw new EntityNotFoundException("User not found!");
    }

    @Override
    public User insert(User user) throws SQLException, EntityNotFoundException {
        // construct & execute query
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO user (username, password, fname, lname, isAdmin) VALUES (?, ?, ?, ?, ?)");
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getUsername());
        stmt.setString(3, user.getFirstName());
        stmt.setString(4, user.getLastName());
        stmt.setBoolean(5, user instanceof AdminUser);
        stmt.executeUpdate();
        // retrieve the created user from database
        return get(user.getUsername());
    }

    @Override
    public boolean delete(User user, User loggedInUser)
            throws SQLException, UnauthorisedAction, EntityNotFoundException {
        // construct & execute query
        if (!(loggedInUser instanceof AdminUser)) {
            throw new UnauthorisedAction("You're unauthorised to delete a user!");
        }
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM user WHERE username = ?");
        stmt.setString(1, user.getUsername());
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new EntityNotFoundException("[ERROR-DB] User not found!");
        }
        return true;
    }
}
