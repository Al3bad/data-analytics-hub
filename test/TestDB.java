package test;

import org.junit.*;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import dev.alabbad.exceptions.UnauthorisedAction;
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
        DB.createUserTable();
        DB.createPostTable();
    }

    // ==================================================
    // --> Create tables
    // ==================================================
    @Test
    public void createUserTableTest() {
        assertTrue(DB.createUserTable());
    }

    @Test
    public void createPostTableTest() {
        assertTrue(DB.createPostTable());
    }

    @Test
    public void createPostTableTest_Faild() throws SQLException {
        DB.getConnection().close();
        assertFalse(DB.createPostTable());
    }

    // ==================================================
    // --> Users: Insert user
    // ==================================================
    @Test
    public void insertUserTest_User() throws SQLException, UserNotFoundException {
        User user = DB.insertUser("username", "123456789", "First", "Last", false);
        assertTrue(user instanceof User);
        assertEquals("username", user.getUsername());
    }

    @Test
    public void insertUserTest_AdminUser() throws SQLException, UserNotFoundException {
        User adminUser = DB.insertUser("admin", "admin", "First", "Last", true);
        assertTrue(adminUser instanceof AdminUser);
        assertEquals("admin", adminUser.getUsername());
    }

    @Test(expected = SQLException.class)
    public void insertUserTest_User_Exception() throws SQLException, UserNotFoundException {
        DB.insertUser(null, "123456789", "First", "Last", false);
    }

    @Test(expected = SQLException.class)
    public void getUserTest_Excpetion() throws SQLException, UserNotFoundException {
        DB.insertUser("username", "123456789", "First", "Last", false);
        DB.insertUser("username", "123456789", "First", "Last", false);
    }

    // ==================================================
    // --> Users: Get user
    // ==================================================
    @Test
    public void getUserTest() throws SQLException, UserNotFoundException {
        DB.insertUser("username", "123456789", "First", "Last", false);
        User user2 = DB.getUser("username");
        assertTrue(user2 instanceof User);
        assertEquals("username", user2.getUsername());
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserTest_UserNotFoundException() throws SQLException, UserNotFoundException {
        DB.getUser("username");
    }

    // ==================================================
    // --> Users: Get all users
    // ==================================================
    @Test
    public void getAllUsersTest() throws SQLException, UserNotFoundException {
        HashMap<String, User> users;
        users = DB.getAllUsers();
        assertEquals(0, users.size());
        DB.insertUser("username1", "123456789", "First", "Last", false);
        DB.insertUser("username2", "123456789", "First", "Last", false);
        DB.insertUser("username3", "123456789", "First", "Last", false);
        DB.insertUser("username4", "123456789", "First", "Last", false);
        DB.insertUser("username5", "123456789", "First", "Last", false);
        users = DB.getAllUsers();
        assertEquals(5, users.size());
    }

    // ==================================================
    // --> Users: Update user
    // ==================================================
    @Test
    public void updateUserTest() throws SQLException, UserNotFoundException, UnauthorisedAction {
        User updatedUser = DB.updateUser("username", "newusername", "123456789", "123456789", "First", "Last");
        assertEquals("newusername", updatedUser.getUsername());
    }
}
