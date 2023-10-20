package test;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;

import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.exceptions.DatabaseConnectionException;
import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.models.AdminUser;
import dev.alabbad.models.DB;
import dev.alabbad.models.Model;
import dev.alabbad.models.User;
import dev.alabbad.models.VIPUser;

public class TestUserDao {
    static int testCount;
    DB db;

    @BeforeClass
    public static void beforeAll() {
        testCount = 0;
    }

    @Before
    public void beforeEach() throws DatabaseConnectionException {
        System.out.println("\n--> Test " + ++TestUserDao.testCount);
        this.db = new DB();
        this.db.connect("jdbc:sqlite::memory:");
        Model.init(this.db.getConnection());
        Model.getUserDao().createTable();
    }

    // ==================================================
    // --> Create tables
    // ==================================================
    @Test
    public void createUserTableTest() {
        assertTrue(Model.getUserDao().createTable());
    }

    @Test
    public void createUserTableTest_Faild() throws SQLException {
        this.db.getConnection().close();
        assertFalse(Model.getUserDao().createTable());
    }

    // ==================================================
    // --> Users: Insert user
    // ==================================================
    @Test
    public void insertUserTest_User() throws SQLException, EntityNotFoundException {
        User user = Model.getUserDao().insert(new User("username", "123456789", "First", "Last"));
        assertTrue(user instanceof User);
        assertEquals("username", user.getUsername());
    }

    @Test
    public void insertUserTest_AdminUser() throws SQLException, EntityNotFoundException {
        User adminUser = Model.getUserDao().insert(new AdminUser("admin", "admin", "First", "Last"));
        assertTrue(adminUser instanceof AdminUser);
        assertEquals("admin", adminUser.getUsername());
    }

    @Test(expected = SQLException.class)
    public void insertUserTest_User_Exception() throws SQLException, EntityNotFoundException {
        Model.getUserDao().insert(new User(null, "123456789", "First", "Last"));
    }

    @Test(expected = SQLException.class)
    public void getUserTest_Excpetion() throws SQLException, EntityNotFoundException {
        Model.getUserDao().insert(new User("username", "123456789", "First", "Last"));
        Model.getUserDao().insert(new User("username", "123456789", "First", "Last"));
    }

    // ==================================================
    // --> Users: Get user
    // ==================================================
    @Test
    public void getUserTest() throws SQLException, EntityNotFoundException {
        Model.getUserDao().insert(new User("username", "123456789", "First", "Last"));
        User user = Model.getUserDao().get("username");
        assertTrue(user instanceof User);
        assertEquals("username", user.getUsername());
    }

    @Test(expected = EntityNotFoundException.class)
    public void getUserTest_UserNotFoundException() throws SQLException, EntityNotFoundException {
        Model.getUserDao().get("username");
    }

    // ==================================================
    // --> Users: Get all users
    // ==================================================
    @Test
    public void getAllUsersTest() throws SQLException, EntityNotFoundException {
        HashMap<String, User> users;
        users = Model.getUserDao().getAll();
        assertEquals(0, users.size());
        Model.getUserDao().insert(new User("username1", "123456789", "First", "Last"));
        Model.getUserDao().insert(new User("username2", "123456789", "First", "Last"));
        Model.getUserDao().insert(new User("username3", "123456789", "First", "Last"));
        Model.getUserDao().insert(new User("username4", "123456789", "First", "Last"));
        Model.getUserDao().insert(new User("username5", "123456789", "First", "Last"));
        users = Model.getUserDao().getAll();
        assertEquals(5, users.size());
    }

    // ==================================================
    // --> Users: Update user
    // ==================================================
    @Test
    public void updateUserTest() throws SQLException, EntityNotFoundException, UnauthorisedAction {
        // add a users to DB
        Model.getUserDao().insert(new User("username", "123456789", "First", "Last"));
        Model.getUserDao().insert(new User("username1", "123456789", "First", "Last"));
        User updatedUser = Model.getUserDao().update(new User("newusername", "newpassword", "First", "Last"),
                        "username", "123456789");
        assertEquals("newusername", updatedUser.getUsername());
        assertTrue(Model.getUserDao().login("newusername", "newpassword") instanceof User);;
    }

    @Test
    public void updateUserTest_withoutPassword() throws SQLException, EntityNotFoundException, UnauthorisedAction {
        // add a users to DB
        Model.getUserDao().insert(new User("username", "123456789", "First", "Last"));
        Model.getUserDao().insert(new User("username1", "123456789", "First", "Last"));
        User updatedUser = Model.getUserDao().update(new User("newusername", null, "First", "Last"), "username",
                        "123456789");
        assertEquals("newusername", updatedUser.getUsername());
        assertTrue(Model.getUserDao().login("newusername", "123456789") instanceof User);;
    }

    // ==================================================
    // --> Users: Delete user
    // ==================================================
    @Test
    public void deleteUserTest_AdminUser() throws SQLException, EntityNotFoundException, UnauthorisedAction {
        // add a users to DB
        AdminUser adminUser = new AdminUser("admin", "123456789", "First", "Last");
        User user1 = new User("username1", "123456789", "First", "Last");
        Model.getUserDao().insert(adminUser);
        Model.getUserDao().insert(user1);
        Model.getUserDao().insert(new User("username2", "123456789", "First", "Last"));
        Model.getUserDao().insert(new User("username3", "123456789", "First", "Last"));
        // check that the user exist before deletion
        assertEquals("username1", Model.getUserDao().get("username1").getUsername());
        // delete user by admin
        assertTrue(Model.getUserDao().delete(user1, adminUser));
    }

    @Test(expected = UnauthorisedAction.class)
    public void deleteUserTest_UnauthorisedUser() throws SQLException, EntityNotFoundException, UnauthorisedAction {
        // add a users to DB
        AdminUser adminUser = new AdminUser("admin", "123456789", "First", "Last");
        User user1 = new User("username1", "123456789", "First", "Last");
        Model.getUserDao().insert(adminUser);
        Model.getUserDao().insert(user1);
        Model.getUserDao().insert(new User("username2", "123456789", "First", "Last"));
        Model.getUserDao().insert(new User("username3", "123456789", "First", "Last"));
        // check that the user exist before deletion
        assertEquals("username1", Model.getUserDao().get("username1").getUsername());
        // delete user
        assertTrue(Model.getUserDao().delete(adminUser, adminUser));
    }

    // ==================================================
    // --> Users: Login user
    // ==================================================
    @Test
    public void loginUserTest() throws SQLException, EntityNotFoundException, UnauthorisedAction {
        // add a users to DB
        AdminUser adminUser = new AdminUser("admin", "123456789", "First", "Last");
        User user1 = new User("username1", "123456789", "First", "Last");
        Model.getUserDao().insert(adminUser);
        Model.getUserDao().insert(user1);
        Model.getUserDao().insert(new User("username2", "123456789", "First", "Last"));
        Model.getUserDao().insert(new User("username3", "123456789", "First", "Last"));
        // loggin nomral and admin users
        User loggedinUser = Model.getUserDao().login("username1", "123456789");
        User loggedinAdmin = Model.getUserDao().login("admin", "123456789");

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
        AdminUser adminUser = new AdminUser("admin", "123456789", "First", "Last");
        User user1 = new User("username1", "123456789", "First", "Last");
        Model.getUserDao().insert(adminUser);
        Model.getUserDao().insert(user1);
        Model.getUserDao().insert(new User("username2", "123456789", "First", "Last"));
        Model.getUserDao().insert(new User("username3", "123456789", "First", "Last"));
        // trying logging in with incorrect password
        Model.getUserDao().login("username1", "invalidpassword");
    }

    @Test(expected = EntityNotFoundException.class)
    public void loginUserTest_EntityNotFoundException()
                    throws SQLException, EntityNotFoundException, UnauthorisedAction {
        // add a users to DB
        Model.getUserDao().insert(new User("username1", "123456789", "First", "Last"));
        // trying logging in with incorrect password
        Model.getUserDao().login("usernotfound", "invalidpassword");
    }

    // ==================================================
    // --> Users: Upgrade user
    // ==================================================
    @Test
    public void upgradeUserTest() throws SQLException, EntityNotFoundException {
        // add a users to DB
        AdminUser adminUser = new AdminUser("admin", "123456789", "First", "Last");
        User user1 = new User("username1", "123456789", "First", "Last");
        Model.getUserDao().insert(adminUser);
        Model.getUserDao().insert(user1);
        Model.getUserDao().insert(new User("username2", "123456789", "First", "Last"));
        Model.getUserDao().insert(new User("username3", "123456789", "First", "Last"));
        // check type of user before upgrade
        assertTrue(Model.getUserDao().get("username1") instanceof User);
        // upgrade user
        assertTrue(Model.getUserDao().upgrade(user1) instanceof VIPUser);
    }

    @Test(expected = EntityNotFoundException.class)
    public void upgradeUserTest_EntityNotFoundException() throws SQLException, EntityNotFoundException {
        // add a users to DB
        AdminUser adminUser = new AdminUser("admin", "123456789", "First", "Last");
        User user1 = new User("username1", "123456789", "First", "Last");
        User missingUser = new User("missingUser", "First", "Last");
        Model.getUserDao().insert(adminUser);
        Model.getUserDao().insert(user1);
        Model.getUserDao().insert(new User("username2", "123456789", "First", "Last"));
        Model.getUserDao().insert(new User("username3", "123456789", "First", "Last"));
        // try to upgrade user that doesn't exist
        Model.getUserDao().upgrade(missingUser);
    }

    // ==================================================
    // --> Users: Update user profile image
    // ==================================================
    @Test
    public void updateUserProfileImgTest() throws SQLException, EntityNotFoundException, IOException {
        // add a users to DB
        AdminUser adminUser = new AdminUser("admin", "123456789", "First", "Last");
        User user1 = new User("username1", "123456789", "First", "Last");
        Model.getUserDao().insert(adminUser);
        Model.getUserDao().insert(user1);
        Model.getUserDao().insert(new User("username2", "123456789", "First", "Last"));
        Model.getUserDao().insert(new User("username3", "123456789", "First", "Last"));
        // check that the profile img is null at the start
        assertNull(Model.getUserDao().get("username1").getProfileImg());

        // get the image
        File profileImg = new File("./resources/images/discord-avatar.jpeg");
        InputStream img = new FileInputStream(profileImg);

        // check that the image is not null
        assertNotNull(Model.getUserDao().updateProfileImg("username1", img).getProfileImg());
    }
}
