import java.util.HashMap;
import java.sql.*;

public class DB {
    static Connection conn = null;

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
}
