package test;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.models.AdminUser;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;
import dev.alabbad.models.VIPUser;

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
    public void insertUserTest_User() throws SQLException, EntityNotFoundException {
        User user = DB.insertUser("username", "123456789", "First", "Last", false);
        assertTrue(user instanceof User);
        assertEquals("username", user.getUsername());
    }

    @Test
    public void insertUserTest_AdminUser() throws SQLException, EntityNotFoundException {
        User adminUser = DB.insertUser("admin", "admin", "First", "Last", true);
        assertTrue(adminUser instanceof AdminUser);
        assertEquals("admin", adminUser.getUsername());
    }

    @Test(expected = SQLException.class)
    public void insertUserTest_User_Exception() throws SQLException, EntityNotFoundException {
        DB.insertUser(null, "123456789", "First", "Last", false);
    }

    @Test(expected = SQLException.class)
    public void getUserTest_Excpetion() throws SQLException, EntityNotFoundException {
        DB.insertUser("username", "123456789", "First", "Last", false);
        DB.insertUser("username", "123456789", "First", "Last", false);
    }

    // ==================================================
    // --> Users: Get user
    // ==================================================
    @Test
    public void getUserTest() throws SQLException, EntityNotFoundException {
        DB.insertUser("username", "123456789", "First", "Last", false);
        User user2 = DB.getUser("username");
        assertTrue(user2 instanceof User);
        assertEquals("username", user2.getUsername());
    }

    @Test(expected = EntityNotFoundException.class)
    public void getUserTest_UserNotFoundException() throws SQLException, EntityNotFoundException {
        DB.getUser("username");
    }

    // ==================================================
    // --> Users: Get all users
    // ==================================================
    @Test
    public void getAllUsersTest() throws SQLException, EntityNotFoundException {
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
    public void updateUserTest() throws SQLException, EntityNotFoundException, UnauthorisedAction {
        // add a users to DB
        DB.insertUser("username", "123456789", "First", "Last", false);
        DB.insertUser("username1", "123456789", "First", "Last", false);
        User updatedUser = DB.updateUser("username", "newusername", "123456789", "123456789", "First", "Last");
        assertEquals("newusername", updatedUser.getUsername());
    }

    // ==================================================
    // --> Users: Delete user
    // ==================================================
    @Test
    public void deleteUserTest_AdminUser() throws SQLException, EntityNotFoundException, UnauthorisedAction {
        // add a users to DB
        DB.insertUser("admin", "123456789", "First", "Last", true);
        DB.insertUser("username", "123456789", "First", "Last", false);
        DB.insertUser("username1", "123456789", "First", "Last", false);
        DB.insertUser("username2", "123456789", "First", "Last", false);
        // check that the user exist before deletion
        assertEquals("username1", DB.getUser("username1").getUsername());
        // delete user by admin
        assertTrue(DB.deleteUser("username1", "admin"));
    }

    @Test(expected = UnauthorisedAction.class)
    public void deleteUserTest_UnauthorisedUser() throws SQLException, EntityNotFoundException, UnauthorisedAction {
        // add a users to DB
        DB.insertUser("admin", "123456789", "First", "Last", true);
        DB.insertUser("username", "123456789", "First", "Last", false);
        DB.insertUser("username1", "123456789", "First", "Last", false);
        DB.insertUser("username2", "123456789", "First", "Last", false);
        // check that the user exist before deletion
        assertEquals("username1", DB.getUser("username1").getUsername());
        // delete user
        assertTrue(DB.deleteUser("username1", "username1"));
    }

    // ==================================================
    // --> Users: Login user
    // ==================================================
    @Test
    public void loginUserTest() throws SQLException, EntityNotFoundException, UnauthorisedAction {
        // add a users to DB
        DB.insertUser("admin", "123456789", "First", "Last", true);
        DB.insertUser("username", "123456789", "First", "Last", false);
        DB.insertUser("username1", "123456789", "First", "Last", false);
        DB.insertUser("username2", "123456789", "First", "Last", false);

        // loggin nomral and admin users
        User loggedinUser = DB.loginUser("username1", "123456789");
        User loggedinAdmin = DB.loginUser("admin", "123456789");

        // check type of user
        assertTrue(loggedinUser instanceof User);
        assertTrue(loggedinAdmin instanceof AdminUser);

        // check the username
        assertEquals("username1", loggedinUser.getUsername());
        assertEquals("admin", loggedinAdmin.getUsername());
    }

    @Test(expected = UnauthorisedAction.class)
    public void loginUserTest_UnauthorisedAction() throws SQLException, EntityNotFoundException, UnauthorisedAction {
        // add a users to DB
        DB.insertUser("admin", "123456789", "First", "Last", true);
        DB.insertUser("username", "123456789", "First", "Last", false);
        DB.insertUser("username1", "123456789", "First", "Last", false);
        DB.insertUser("username2", "123456789", "First", "Last", false);

        // trying logging in with incorrect password
        DB.loginUser("username1", "invalidpassword");
    }

    @Test(expected = EntityNotFoundException.class)
    public void loginUserTest_UserNotFoundException() throws SQLException, EntityNotFoundException, UnauthorisedAction {
        // add a users to DB
        DB.insertUser("admin", "123456789", "First", "Last", true);
        DB.insertUser("username", "123456789", "First", "Last", false);
        DB.insertUser("username1", "123456789", "First", "Last", false);
        DB.insertUser("username2", "123456789", "First", "Last", false);

        // trying logging in with incorrect password
        DB.loginUser("usernotfound", "invalidpassword");
    }

    // ==================================================
    // --> Users: Upgrade user
    // ==================================================
    @Test
    public void upgradeUserTest() throws SQLException, EntityNotFoundException {
        // add a users to DB
        DB.insertUser("admin", "123456789", "First", "Last", true);
        DB.insertUser("username", "123456789", "First", "Last", false);
        DB.insertUser("username2", "123456789", "First", "Last", false);

        // check type of user before upgrade
        assertTrue(DB.getUser("username") instanceof User);

        // upgrade user
        assertTrue(DB.upgradeUser("username") instanceof VIPUser);
    }

    @Test(expected = EntityNotFoundException.class)
    public void upgradeUserTest_UserNotFoundException() throws SQLException, EntityNotFoundException {
        // add a users to DB
        DB.insertUser("admin", "123456789", "First", "Last", true);
        DB.insertUser("username", "123456789", "First", "Last", false);
        DB.insertUser("username2", "123456789", "First", "Last", false);
        // try to upgrade user that doesn't exist
        DB.upgradeUser("missinguser");
    }

    // ==================================================
    // --> Users: Update user profile image
    // ==================================================
    @Test
    public void updateUserProfileImgTest() throws SQLException, EntityNotFoundException, IOException {
        // add a users to DB
        DB.insertUser("admin", "123456789", "First", "Last", true);
        DB.insertUser("username", "123456789", "First", "Last", false);
        DB.insertUser("username2", "123456789", "First", "Last", false);

        // check that the profile img is null at the start
        assertNull(DB.getUser("username").getProfileImg());

        // get the image
        File profileImg = new File("./resources/images/discord-avatar.jpeg");
        InputStream img = new FileInputStream(profileImg);

        // check that the image is not null
        assertNotNull(DB.updateUserProfileImg("username", img).getProfileImg());
    }

    // ==================================================
    // --> Posts: Insert post
    // ==================================================

    // ==================================================
    // --> Posts: Get post
    // ==================================================

    // ==================================================
    // --> Posts: Get posts
    // ==================================================

    // ==================================================
    // --> Posts: Delete post
    // ==================================================

    // ==================================================
    // --> Posts: Get shares distribution
    // ==================================================
}
