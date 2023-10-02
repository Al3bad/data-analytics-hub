package test;

import org.junit.*;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import dev.alabbad.exceptions.UserNotFoundException;
import dev.alabbad.models.AdminUser;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;

public class TestDB {
    static int testCount;
    Connection conn;

    @BeforeClass
    public static void beforeAll() {
        testCount = 0;
    }

    @Before
    public void beforeEach() throws SQLException {
        System.out.println("\n--> Test " + ++TestDB.testCount);
        DB.connect(":memory:");
        this.conn = DB.getConnection();
    }

    // ==================================================
    // --> Create tables
    // ==================================================
    @Test
    public void testCreateUserTable() {
        assertTrue(DB.createUserTable());
    }

    @Test
    public void testCreatePostTable() {
        assertTrue(DB.createPostTable());
    }

    @Test
    public void testCreatePostTable_Faild() throws SQLException {
        DB.getConnection().close();
        assertFalse(DB.createPostTable());
    }

    // ==================================================
    // --> Users: Insert user
    // ==================================================
    public void testInsertUser_User() throws SQLException, UserNotFoundException {
        User user = DB.insertUser("username", "123456789", "First", "Last", false);
        assertTrue(user instanceof User);
        assertEquals("username", user.getUsername());
    }

    public void testInsertUser_AdminUser() throws SQLException, UserNotFoundException {
        User adminUser = DB.insertUser("admin", "admin", "First", "Last", true);
        assertTrue(adminUser instanceof AdminUser);
        assertEquals("admin", adminUser.getUsername());
    }

    public void testInsertUser_User_Exception() throws SQLException, UserNotFoundException {
        User user = DB.insertUser(null, "123456789", "First", "Last", false);
        assertTrue(user instanceof User);
        assertEquals("username", user.getUsername());
    }

    // ==================================================
    // --> Users: Get user
    // ==================================================
}
