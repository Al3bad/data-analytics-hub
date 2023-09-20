package dev.alabbad.models;

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

public class DB {
    private static Connection conn = null;

    /**
     * Connect to a sample database
     *
     * @throws ClassNotFoundException
     */
    public static void connect(String sqliteFilename) {
        String url = "jdbc:sqlite:" + sqliteFilename;
        try {
            // create a connection to the sqlite DB
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established!");
        } catch (SQLException e) {
            System.out.println("SQLiteError: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    public static void createUserTable() {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("""
                            CREATE TABLE IF NOT EXISTS user (
                                username TEXT UNIQUE NOT NULL PRIMARY KEY CHECK(TRIM(username) != ''),
                                password TEXT NOT NULL CHECK(LENGTH(password) > 3),
                                fname TEXT NOT NULL CHECK(TRIM(fname) != ''),
                                lname TEXT NOT NULL CHECK(TRIM(lname) != ''),
                                isAdmin INTEGER NOT NULL DEFAULT 0,
                                isVIP INTEGER NOT NULL DEFAULT 0
                            );
                            """);
        } catch (SQLException e) {
            System.out.println("SQLiteError: " + e.getMessage());
        }
    }

    public static void createPostTable() {
        try {
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
        } catch (SQLException e) {
            System.out.println("SQLiteError: " + e.getMessage());
        }
    }

    public static User insertUser(String username, String password, String fname, String lname, Boolean isAdmin)
                    throws SQLException, UserNotFoundException {
        PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO user (username, password, fname, lname, isAdmin) VALUES (?, ?, ?, ?, ?)");
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.setString(3, fname);
        stmt.setString(4, lname);
        stmt.setBoolean(5, isAdmin);
        stmt.executeUpdate();
        return getUser(username);
    }

    public static Post insertPost(Integer id, String content, String author, int likes, int shares, String dateTime)
                    throws SQLException {
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

    public static Post getPost(int id) throws PostNotFoundException, SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM post WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
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
     * @param sortBy the field that the posts should be sorted by. Accepts either
     * `likes` or `shares`
     * @param limit limit the number of post to return
     * @return collection of posts
     * @throws InvalidArgumentException when the argument of `sortBy` is neither
     * `likes` nor `shares`
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

        // prepare sqlite query
        PreparedStatement stmt = conn.prepareStatement(stmtStr);
        if (author.length() > 0) {
            stmt.setString(1, author);
            stmt.setInt(2, limit);
        } else {
            stmt.setInt(1, limit);
        }
        ArrayList<Post> posts = new ArrayList<>();
        // run query
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int pID = rs.getInt("id");
            String pAuthor = rs.getString("author");
            String pContent = rs.getString("content");
            int pLikes = rs.getInt("likes");
            int pShares = rs.getInt("shares");
            String pDateTime = rs.getString("dateTime");
            posts.add(new Post(pID, pContent, pAuthor, pLikes, pShares, pDateTime));
        }
        // return posts
        return posts;
    }

    public static Boolean deletePost(int id, String postAuthor, String loggedinUsername)
                    throws PostNotFoundException, SQLException, UnauthorisedAction, UserNotFoundException {
        Boolean isAdmin = getUser(loggedinUsername) instanceof AdminUser;
        Post post = getPost(id);
        if (!isAdmin && !post.getAuthor().toLowerCase().equals(postAuthor.toLowerCase())) {
            throw new UnauthorisedAction("You're unauthorised to delete a posts of someone else's post!");
        }
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

    public static Boolean deletePosts(String postAuthor, String loggedinUsername) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM post WHERE LOWER(author) = LOWER(?)");
        stmt.setString(1, postAuthor);
        stmt.executeUpdate();
        return true;
    }

    public static Boolean deleteUser(String username, String loggedinUsername)
                    throws SQLException, UserNotFoundException, UnauthorisedAction {
        if (!(getUser(loggedinUsername) instanceof AdminUser)) {
            throw new UnauthorisedAction("You're unauthorised to delete a user!");
        }
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM user WHERE username = ?");
        stmt.setString(1, username);
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new UserNotFoundException("[ERROR-DB] User not found!");
        }
        DB.deletePosts(username, loggedinUsername);
        return true;
    }

    public static int getLastInsertedRowID() throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT last_insert_rowid() as id");
        if (!rs.next()) {
            throw new SQLException("Last inserted ID is not found!");
        }
        return rs.getInt("id");
    }

    public static User updateUser(String currentUsername, String username, String password, String fname, String lname)
                    throws UserNotFoundException {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                            "UPDATE user SET username=?,password=?,fname=?,lname=? WHERE LOWER(username) = LOWER(?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, fname);
            stmt.setString(4, lname);
            stmt.setString(5, currentUsername);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new UserNotFoundException("[ERROR-DB] User not found!");
            }
            // Update author field for the posts
            PreparedStatement stmt2 = conn.prepareStatement("UPDATE post SET author=? WHERE LOWER(author) = LOWER(?)");
            stmt2.setString(1, username);
            stmt2.setString(2, currentUsername);
            stmt2.executeUpdate();
            return getUser(username);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static HashMap<String, User> getAllUsers() {
        HashMap<String, User> users = new HashMap<String, User>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username, fname, lname FROM user WHERE isAdmin = 0");
            while (rs.next()) {
                String username = rs.getString("username");
                String fname = rs.getString("fname");
                String lname = rs.getString("lname");
                users.put(username, new User(username, fname, lname));
            }
            return users;
        } catch (SQLException e) {
            System.out.println("SQLiteError: " + e.getMessage());
            return null;
        }
    }

    public static User getUser(String username) throws SQLException, UserNotFoundException {
        PreparedStatement stmt = conn
                        .prepareStatement("SELECT username, fname, lname, isVIP, isAdmin FROM user WHERE username = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String user = rs.getString("username");
            String fname = rs.getString("fname");
            String lname = rs.getString("lname");
            Boolean isVIP = rs.getBoolean("isVIP");
            Boolean isAdmin = rs.getBoolean("isAdmin");
            if (isAdmin) {
                return new AdminUser(user, fname, lname);
            } else if (isVIP) {
                return new VIPUser(user, fname, lname);
            }
            return new User(user, fname, lname);
        }
        throw new UserNotFoundException("User not found!");
    }

    public static User loginUser(String username, String password) throws UserNotFoundException, SQLException {
        User user = getUser(username);
        PreparedStatement stmt = conn.prepareStatement(
                        "SELECT username, fname, lname, isVIP, isAdmin FROM user WHERE username = ? AND password = ?");
        stmt.setString(1, user.getUsername());
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            return user;
        }
        return null;
    }

    public static VIPUser upgradeUser(User user) throws UserNotFoundException, SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE user SET isVIP=1 WHERE LOWER(username) = LOWER(?)");
        stmt.setString(1, user.getUsername());
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new UserNotFoundException("[ERROR-DB] User not found!");
        }
        return new VIPUser(user.getUsername(), user.getFirstName(), user.getLastName());
    }

    public static ArrayList<Integer> getSharesDistribution() throws SQLException {
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
            shareDistribution.add(rs.getInt("shares-0-99"));
            shareDistribution.add(rs.getInt("shares-100-999"));
            shareDistribution.add(rs.getInt("shares-gt-999"));
            return shareDistribution;
        }
        return shareDistribution;
    }
}
