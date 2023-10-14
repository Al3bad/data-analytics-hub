package test;

import org.junit.*;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.models.DB;
import dev.alabbad.models.Model;
import dev.alabbad.models.Post;

public class TestPostDao {
    static int testCount;
    static Connection connection;;

    @BeforeClass
    public static void beforeAll() {
        testCount = 0;
    }

    @Before
    public void beforeEach() throws SQLException {
        System.out.println("\n--> Test " + ++TestUserDao.testCount);
        this.connection = DB.connect(":memory:");
        Model.init(connection);
        Model.getUserDao().createTable();
    }

    // ==================================================
    // --> Create tables
    // ==================================================
    @Test
    public void createPostTableTest() {
        assertTrue(Model.getPostDao().createTable());
    }

    @Test
    public void createPostTableTest_Faild() throws SQLException {
        DB.getConnection().close();
        assertFalse(Model.getPostDao().createTable());
    }

    // ==================================================
    // --> Posts: Insert
    // ==================================================
    @Test
    public void insertPostTest() throws SQLException, EntityNotFoundException {
        // int ID, String content, String author, int likes, int shares, String dateTime
        Post post = Model.getPostDao().insert(new Post(123, "Hello World!", "xv", 22, 100, "10/10/2022 12:12"));
        assertEquals((Integer) 123, post.getID());
    }

    // ==================================================
    // --> Posts: Get
    // ==================================================
    @Test
    public void getPostTest() throws SQLException, EntityNotFoundException {
        Model.getPostDao().insert(new Post(123, "Hello World!", "xv", 22, 100, "10/10/2022 12:12"));
        Post post = Model.getPostDao().get(123);
        assertTrue(post instanceof Post);
        assertEquals((Integer) 123, post.getID());
    }

    @Test(expected = EntityNotFoundException.class)
    public void getUserTest_UserNotFoundException() throws SQLException, EntityNotFoundException {
        Model.getPostDao().get(222);
    }
}
