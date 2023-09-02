package dev.alabbad.models;

import java.util.HashMap;

import dev.alabbad.exceptions.UserNotFoundException;

import java.sql.*;

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
            stmt.execute("CREATE TABLE IF NOT EXISTS user ("
                            + "username TEXT UNIQUE NOT NULL PRIMARY KEY CHECK(TRIM(username) != ''),"
                            + "password TEXT NOT NULL CHECK(LENGTH(password) > 6),"
                            + "fname TEXT NOT NULL CHECK(LENGTH(fname) > 2),"
                            + "lname TEXT NOT NULL CHECK(LENGTH(lname) > 2)" + ")");
        } catch (SQLException e) {
            System.out.println("SQLiteError: " + e.getMessage());
        }
    }

    public static User insertUser(String username, String password, String fname, String lname) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO user (username, password, fname, lname) VALUES (?, ?, ?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, fname);
            stmt.setString(4, lname);
            stmt.executeUpdate();
            return new User(username, fname, lname);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
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
                throw new UserNotFoundException("[ERROR-DB] User does not exists!");
            }
            return new User(username, fname, lname);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static HashMap<String, User> getAllUsers() {
        HashMap<String, User> users = new HashMap<String, User>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username, fname, lname FROM user");
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

    public static User loginUser(String username, String password) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                            "SELECT username, fname, lname FROM user WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String user = rs.getString("username");
                String fname = rs.getString("fname");
                String lname = rs.getString("lname");
                return new User(user, fname, lname);
            }
            return null;
        } catch (SQLException e) {
            System.out.println("SQLiteError: " + e.getMessage());
            return null;
        }
    }

    public static Post insertPost(String content, String username, int likes, int shares, String dateTime) {
        // TODO:
        return new Post(1, content, username, likes, shares, dateTime);
    }
}
