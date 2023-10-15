package dev.alabbad.models;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.exceptions.EntityNotFoundException;

/**
 * The data access object for the users
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class UserDao implements Dao<String, User> {
    private Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Create user table if not exist in the database
     *
     * @return `true` if no errors, `false` otherwise
     */
    @Override
    public boolean createTable() {
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

    /**
     * Get user by username
     *
     * @param username
     * @throws EntityNotFoundException when the user is not found
     * @throws SQLException
     */
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

    /**
     * Get all users
     *
     * @return users
     * @throws SQLException
     */
    public HashMap<String, User> getAll() throws SQLException {
        // construct & execute query
        Statement stmt = connection.createStatement();
        HashMap<String, User> users = new HashMap<String, User>();
        ResultSet rs = stmt.executeQuery("SELECT username, fname, lname, isAdmin FROM user");
        while (rs.next()) {
            // extract data from result
            String username = rs.getString("username");
            String fname = rs.getString("fname");
            String lname = rs.getString("lname");
            Boolean isAdmin = rs.getBoolean("isAdmin");
            // create the correct user object based on the extracted flags
            if (isAdmin) {
                users.put(username, new AdminUser(username, fname, lname));
            } else {
                users.put(username, new User(username, fname, lname));
            }
        }
        return users;
    }

    /**
     * Insert new user
     *
     * @param user
     * @return new User object
     * @throws SQLException
     * @throws EntityNotFoundException
     */
    @Override
    public User insert(User user) throws SQLException, EntityNotFoundException {
        // construct & execute query
        PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO user (username, password, fname, lname, isAdmin) VALUES (?, ?, ?, ?, ?)");
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPassword());
        stmt.setString(3, user.getFirstName());
        stmt.setString(4, user.getLastName());
        stmt.setBoolean(5, user instanceof AdminUser);
        stmt.executeUpdate();
        // retrieve the created user from database
        return get(user.getUsername());
    }

    /**
     * Delete user (only allowed for admin users)
     *
     * @param user to be deleted
     * @param loggedInUser
     * @return true
     * @throws EntityNotFoundException when user is not found
     * @throws UnauthorisedAction when a normal/vip user attempt to delete a user
     * @throws SQLException
     */
    @Override
    public boolean delete(User user, User loggedInUser)
                    throws SQLException, UnauthorisedAction, EntityNotFoundException {
        // construct & execute query
        if (!(loggedInUser instanceof AdminUser) || user instanceof AdminUser) {
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

    /**
     * Update user
     *
     * @param username current username
     * @param newUsername new username
     * @param password current password
     * @param newPassword new password
     * @param fname new first name
     * @param lname new last name
     * @return
     * @throws EntityNotFoundException when user is not found
     * @throws UnauthorisedAction when credentials are incorrect
     * @throws SQLException
     */
    public User update(String username, String newUsername, String password, String newPassword, String fname,
                    String lname) throws EntityNotFoundException, SQLException, UnauthorisedAction {
        // TODO: improve this
        // check authorisation
        login(username, password);
        // construct & execute query
        PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE user SET username=?,password=?,fname=?,lname=? WHERE LOWER(username) = LOWER(?)");
        stmt.setString(1, newUsername);
        stmt.setString(2, newPassword);
        stmt.setString(3, fname);
        stmt.setString(4, lname);
        stmt.setString(5, username);
        stmt.executeUpdate();
        // Update author field for the posts
        PreparedStatement stmt2 = connection
                        .prepareStatement("UPDATE post SET author=? WHERE LOWER(author) = LOWER(?)");
        stmt2.setString(1, newUsername);
        stmt2.setString(2, username);
        stmt2.executeUpdate();
        // return updated user information
        return get(newUsername);
    }

    /**
     * Update isVIP flag for a user
     *
     * @param user
     * @return updated user
     * @throws EntityNotFoundException when user is not found in the database
     * @throws SQLException
     */
    public User upgrade(User user) throws EntityNotFoundException, SQLException {
        // construct & execute query
        PreparedStatement stmt = connection
                        .prepareStatement("UPDATE user SET isVIP=1 WHERE LOWER(username) = LOWER(?)");
        stmt.setString(1, user.getUsername());
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new EntityNotFoundException("[ERROR-DB] User not found!");
        }
        return get(user.getUsername());
    }

    /**
     * Login user by checking the provided credentials
     *
     * @param username
     * @param password
     * @throws EntityNotFoundException when user is not found in the database
     * @throws UnauthorisedAction when credentials are incorrect
     * @throws SQLException
     */
    public User login(String username, String password)
                    throws EntityNotFoundException, SQLException, UnauthorisedAction {
        // check if the user exists in the database first
        User user = get(username);
        // construct & execute query
        PreparedStatement stmt = connection.prepareStatement(
                        "SELECT username, fname, lname, isVIP, isAdmin FROM user WHERE username = ? AND password = ?");
        stmt.setString(1, user.getUsername());
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            return user;
        }
        throw new UnauthorisedAction("Incorrect password");
    }

    /**
     * Update profileImg for a user
     *
     * @param username
     * @param profileImg image to be updated
     * @return updated user
     * @throws EntityNotFoundException when user is not found
     * @throws IOException when an error occurs during converting stream to bytes
     * @throws SQLException
     */
    public User updateProfileImg(String username, InputStream profileImg)
                    throws SQLException, EntityNotFoundException, IOException {
        // construct & execute query
        PreparedStatement stmt = connection
                        .prepareStatement("UPDATE user SET profileImg = ? WHERE LOWER(username) = LOWER(?)");
        stmt.setBytes(1, streamToBytes(profileImg));
        stmt.setString(2, username);
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new EntityNotFoundException("[ERROR-DB] User not found!");
        }
        return get(username);
    }

    /**
     * Convert stream to bytes (to store blob in database)
     *
     * @param file file stream to be converted
     * @return bytes array or null
     * @throws IOException
     */
    private static byte[] streamToBytes(InputStream file) throws IOException {
        // reference: https://www.sqlitetutorial.net/sqlite-java/jdbc-read-write-blob/
        ByteArrayOutputStream bos = null;
        byte[] buffer = new byte[1024];
        bos = new ByteArrayOutputStream();
        for (int len; (len = file.read(buffer)) != -1;) {
            bos.write(buffer, 0, len);
        }
        return bos != null ? bos.toByteArray() : null;
    }
}
