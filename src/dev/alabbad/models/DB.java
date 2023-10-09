package dev.alabbad.models;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import dev.alabbad.exceptions.InvalidArgumentException;
import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.exceptions.UserNotFoundException;

/**
 * This class holds a set of static methods to interact with the database
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class DB {
    public static final int SQLITE_CONSTRAINT = 19;
    private static Connection conn = null;

    public static Connection getConnection() {
        return conn;
    }

    /**
     * Connect to the database.
     *
     * @param sqliteFilename
     * @throws SQLException
     */
    public static void connect(String sqliteFilename) throws SQLException {
        String url = "jdbc:sqlite:" + sqliteFilename;
        // create a connection to the sqlite DB
        conn = DriverManager.getConnection(url);
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

    /**
     * Get the ID of the last inserted row in the database
     *
     * @return ID number
     * @throws SQLException when no ID is found
     */
    public static int getLastInsertedRowID() throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT last_insert_rowid() as id");
        if (!rs.next()) {
            throw new SQLException("Last inserted ID is not found!");
        }
        return rs.getInt("id");
    }

    // ==================================================
    // --> Create tables
    // ==================================================

    /**
     * Create user table if not exist in the database
     *
     * @return `true` if no errors, `false` otherwise
     */
    public static Boolean createUserTable() {
        try {
            // construct & execute query
            Statement stmt = conn.createStatement();
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
     * Create post table if not exist in the database
     *
     * @return `true` if no errors, `false` otherwise
     */
    public static Boolean createPostTable() {
        try {
            // construct & execute query
            Statement stmt = conn.createStatement();
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

    // ==================================================
    // --> Users operations
    // ==================================================

    /**
     * Insert new user
     *
     * @param username
     * @param password
     * @param fname
     * @param lname
     * @param isAdmin
     * @return new User object
     * @throws UserNotFoundException when the user is not found after the insert
     *                               operation
     * @throws SQLException
     */
    public static User insertUser(String username, String password, String fname, String lname, Boolean isAdmin)
            throws SQLException, UserNotFoundException {
        // construct & execute query
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO user (username, password, fname, lname, isAdmin) VALUES (?, ?, ?, ?, ?)");
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.setString(3, fname);
        stmt.setString(4, lname);
        stmt.setBoolean(5, isAdmin);
        stmt.executeUpdate();
        // retrieve the created user from database
        return getUser(username);
    }

    /**
     * Get user by username
     *
     * @param username
     * @throws UserNotFoundException when the user is not found
     * @throws SQLException
     */
    public static User getUser(String username) throws SQLException, UserNotFoundException {
        // construct & execute query
        PreparedStatement stmt = conn.prepareStatement(
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
        throw new UserNotFoundException("User not found!");
    }

    /**
     * Get all users
     *
     * @return users
     * @throws SQLException
     */
    public static HashMap<String, User> getAllUsers() throws SQLException {
        // construct & execute query
        Statement stmt = conn.createStatement();
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
     * @param username    current username
     * @param newUsername new username
     * @param password    current password
     * @param newPassword new password
     * @param fname       new first name
     * @param lname       new last name
     * @return
     * @throws UserNotFoundException when user is not found
     * @throws UnauthorisedAction    when credentials are incorrect
     * @throws SQLException
     */
    public static User updateUser(String username, String newUsername, String password, String newPassword,
            String fname, String lname) throws UserNotFoundException, SQLException, UnauthorisedAction {
        // check authorisation
        loginUser(username, password);
        // construct & execute query
        PreparedStatement stmt = conn.prepareStatement(
                "UPDATE user SET username=?,password=?,fname=?,lname=? WHERE LOWER(username) = LOWER(?)");
        stmt.setString(1, newUsername);
        stmt.setString(2, newPassword);
        stmt.setString(3, fname);
        stmt.setString(4, lname);
        stmt.setString(5, username);
        stmt.executeUpdate();
        // Update author field for the posts
        PreparedStatement stmt2 = conn.prepareStatement("UPDATE post SET author=? WHERE LOWER(author) = LOWER(?)");
        stmt2.setString(1, newUsername);
        stmt2.setString(2, username);
        stmt2.executeUpdate();
        // return updated user information
        return getUser(newUsername);
    }

    /**
     * Delete user by username (only allowed for admin users)
     *
     * @param username
     * @param loggedinUsername logged in user
     * @return true
     * @throws UserNotFoundException when logged in user is not found in database
     * @throws UnauthorisedAction    when a normal/VIP user attempt to delete a post
     *                               belongs to another user
     * @throws SQLException
     */
    public static Boolean deleteUser(String username, String loggedinUsername)
            throws SQLException, UserNotFoundException, UnauthorisedAction {
        // construct & execute query
        if (!(getUser(loggedinUsername) instanceof AdminUser)) {
            throw new UnauthorisedAction("You're unauthorised to delete a user!");
        }
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM user WHERE username = ?");
        stmt.setString(1, username);
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new UserNotFoundException("[ERROR-DB] User not found!");
        }
        // delete all posts associated to the deleted user
        DB.deletePosts(username);
        return true;
    }

    /**
     * Login user by checking the provided credentials
     *
     * @param username
     * @param password
     * @throws UserNotFoundException when user is not found in the database
     * @throws UnauthorisedAction    when credentials are incorrect
     * @throws SQLException
     */
    public static User loginUser(String username, String password)
            throws UserNotFoundException, SQLException, UnauthorisedAction {
        // check if the user exists in the database first
        User user = getUser(username);
        // construct & execute query
        PreparedStatement stmt = conn.prepareStatement(
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
     * Update isVIP flag for a user
     *
     * @param username
     * @return updated user
     * @throws UserNotFoundException when user is not found in the database
     * @throws SQLException
     */
    public static User upgradeUser(String username) throws UserNotFoundException, SQLException {
        // construct & execute query
        PreparedStatement stmt = conn.prepareStatement("UPDATE user SET isVIP=1 WHERE LOWER(username) = LOWER(?)");
        stmt.setString(1, username);
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new UserNotFoundException("[ERROR-DB] User not found!");
        }
        return getUser(username);
    }

    /**
     * Update profileImg for a user
     *
     * @param username
     * @param profileImg image to be updated
     * @return updated user
     * @throws UserNotFoundException when user is not found
     * @throws IOException           when an error occurs during converting stream
     *                               to bytes
     * @throws SQLException
     */
    public static User updateUserProfileImg(String username, InputStream profileImg)
            throws SQLException, UserNotFoundException, IOException {
        // construct & execute query
        PreparedStatement stmt = conn
                .prepareStatement("UPDATE user SET profileImg = ? WHERE LOWER(username) = LOWER(?)");
        stmt.setBytes(1, streamToBytes(profileImg));
        stmt.setString(2, username);
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new UserNotFoundException("[ERROR-DB] User not found!");
        }
        return getUser(username);
    }

    // ==================================================
    // --> Users operations
    // ==================================================

    /**
     * Insert new post
     *
     * @param id
     * @param content
     * @param author
     * @param likes
     * @param shares
     * @param dateTime
     * @return new Post object
     * @throws SQLException
     */
    public static Post insertPost(Integer id, String content, String author, int likes, int shares, String dateTime)
            throws SQLException {
        // construct & execute query
        PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO post (id,content, author, likes, shares, dateTime)
                VALUES (?, ?, ?, ?, ?, ?);
                """);
        if (id == null) {
            // auto generate id
            stmt.setNull(1, Types.NULL);
        } else {
            // use the provided id
            stmt.setInt(1, id);
        }
        stmt.setString(2, content);
        stmt.setString(3, author);
        stmt.setInt(4, likes);
        stmt.setInt(5, shares);
        stmt.setString(6, dateTime);
        stmt.executeUpdate();
        return new Post(getLastInsertedRowID(), content, author, likes, shares, dateTime);
    }

    /**
     * Get post by its ID
     *
     * @param id post ID
     * @throws PostNotFoundException when post is not found
     * @throws SQLException
     */
    public static Post getPost(int id) throws PostNotFoundException, SQLException {
        // construct & execute query
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM post WHERE id = ?");
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
        throw new PostNotFoundException("[ERROR-DB] Post not found!");
    }

    /**
     * Get top N shared or liked posts
     *
     * @param sortBy the column that the posts should be sorted by. Accepts either
     *               `likes` or `shares`
     * @param limit  limit the number of post to return
     * @return collection of posts
     * @throws InvalidArgumentException when the argument of `sortBy` is neither
     *                                  `likes` nor `shares`
     * @throws SQLException
     */
    public static ArrayList<Post> getPosts(String sortBy, String author, int limit)
            throws InvalidArgumentException, SQLException {
        // all posts for all users or posts for a specific user?
        String stmtStr = "SELECT * FROM post";
        if (author.length() > 0) {
            stmtStr += " WHERE LOWER(author) = LOWER(?)";
        }
        // sort posts by likes or shares
        if (sortBy == "likes" || sortBy == "shares") {
            stmtStr += " ORDER BY " + sortBy + " DESC";
        } else {
            // invalid sortBy
            ArrayList<String> args = new ArrayList<String>();
            args.add("sortBy");
            throw new InvalidArgumentException("Posts can only be filtered by 'likes' or 'shares'", args);
        }
        // set limit
        stmtStr += " LIMIT ?";

        // construct & execute query
        PreparedStatement stmt = conn.prepareStatement(stmtStr);
        if (author.length() > 0) {
            stmt.setString(1, author);
            stmt.setInt(2, limit);
        } else {
            stmt.setInt(1, limit);
        }
        ArrayList<Post> posts = new ArrayList<>();
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            // extract data from result
            int pID = rs.getInt("id");
            String pAuthor = rs.getString("author");
            String pContent = rs.getString("content");
            int pLikes = rs.getInt("likes");
            int pShares = rs.getInt("shares");
            String pDateTime = rs.getString("dateTime");
            posts.add(new Post(pID, pContent, pAuthor, pLikes, pShares, pDateTime));
        }
        return posts;
    }

    /**
     * Delete a post by its ID
     *
     * @param id               post ID
     * @param postAuthor       post author
     * @param loggedinUsername logged in user
     * @return true
     * @throws PostNotFoundException when post is not found in database
     * @throws UnauthorisedAction    when a normal/VIP user attempt to delete a post
     *                               belongs to another user
     * @throws UserNotFoundException when logged in user is not found in database
     * @throws SQLException
     */
    public static Boolean deletePost(int id, String postAuthor, String loggedinUsername)
            throws PostNotFoundException, SQLException, UnauthorisedAction, UserNotFoundException {
        // check for authorisation for this operation
        Boolean isAdmin = getUser(loggedinUsername) instanceof AdminUser;
        Post post = getPost(id);
        if (!isAdmin && !post.getAuthor().toLowerCase().equals(postAuthor.toLowerCase())) {
            throw new UnauthorisedAction("You're unauthorised to delete a posts of someone else's post!");
        }
        // construct & execute query
        String stmtStr = "DELETE FROM post WHERE id = ?";
        if (!(isAdmin)) {
            stmtStr += "  AND LOWER(author) = LOWER(?)";

        }
        PreparedStatement stmt = conn.prepareStatement(stmtStr);
        stmt.setInt(1, id);
        stmt.setString(2, postAuthor);
        stmt.executeUpdate();
        return true;
    }

    /**
     * Delete all posts associated with a username/author
     *
     * @param postAuthor author of the posts
     * @return true
     * @throws SQLException
     */
    private static Boolean deletePosts(String postAuthor) throws SQLException {
        // construct & execute query
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM post WHERE LOWER(author) = LOWER(?)");
        stmt.setString(1, postAuthor);
        stmt.executeUpdate();
        return true;
    }

    /**
     * Get the number of shares for each share category
     *
     * @return array list of number of shares for each category
     * @throws SQLException
     */
    public static ArrayList<Integer> getSharesDistribution() throws SQLException {
        // construct & execute query
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("""
                SELECT * FROM (
                    SELECT COUNT(*) AS "shares-0-99" FROM post WHERE shares < 100
                ) JOIN (
                    SELECT COUNT(*) AS "shares-100-999" FROM post WHERE shares BETWEEN 100 AND 999
                ) JOIN (
                    SELECT COUNT(*) AS "shares-gt-999" FROM post WHERE shares > 999
                )
                """);
        ArrayList<Integer> shareDistribution = new ArrayList<>();
        while (rs.next()) {
            // extract data from result
            shareDistribution.add(rs.getInt("shares-0-99"));
            shareDistribution.add(rs.getInt("shares-100-999"));
            shareDistribution.add(rs.getInt("shares-gt-999"));
            return shareDistribution;
        }
        return shareDistribution;
    }
}
