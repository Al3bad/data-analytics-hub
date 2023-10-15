package test;

import org.junit.*;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.exceptions.InvalidArgumentException;
import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.models.DB;
import dev.alabbad.models.Model;
import dev.alabbad.models.Post;
import dev.alabbad.models.User;

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

    // ==================================================
    // --> Posts: Get some
    // ==================================================
    @Test
    public void getSomePostTest() throws SQLException, EntityNotFoundException, InvalidArgumentException {
        Model.getPostDao().insert(new Post(10, "Hello World!", "username", 509, 623, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(20, "Hello World!", "xv", 84425, 23520, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(30, "Hello World!", "test", 83, 59, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(40, "Hello World!", "xv", 5473, 625, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(50, "Hello World!", "hello", 946, 682, "10/10/2022 12:12"));

        ArrayList<Post> posts;

        // likes
        posts = Model.getPostDao().getSome("likes", null, 3);
        assertEquals(3, posts.size());
        assertEquals((Integer) 20, posts.get(0).getID());
        assertEquals((Integer) 40, posts.get(1).getID());
        assertEquals((Integer) 50, posts.get(2).getID());

        // shares
        posts = Model.getPostDao().getSome("shares", null, 10);
        assertEquals(5, posts.size());
        assertEquals((Integer) 20, posts.get(0).getID());
        assertEquals((Integer) 50, posts.get(1).getID());
        assertEquals((Integer) 40, posts.get(2).getID());

        // shares for a single user
        posts = Model.getPostDao().getSome("shares", "xv", 10);
        assertEquals(2, posts.size());
        assertEquals((Integer) 20, posts.get(0).getID());
        assertEquals((Integer) 40, posts.get(1).getID());
    }

    @Test(expected = InvalidArgumentException.class)
    public void getSomePostTest_InvalidArgumentException()
                    throws SQLException, EntityNotFoundException, InvalidArgumentException {
        Model.getPostDao().getSome("notallowed", null, 10);
    }

    // ==================================================
    // --> Posts: Delete
    // ==================================================
    @Test(expected = EntityNotFoundException.class)
    public void deletePostTest() throws SQLException, EntityNotFoundException, UnauthorisedAction {
        Model.getPostDao().insert(new Post(10, "Hello World!", "username", 509, 623, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(20, "Hello World!", "xv", 84425, 23520, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(30, "Hello World!", "test", 83, 59, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(40, "Hello World!", "xv", 5473, 625, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(50, "Hello World!", "hello", 946, 682, "10/10/2022 12:12"));
        // delete a post
        Model.getPostDao().delete(new Post(20, null, "xv", null, null, null), new User("xv", null, null));

        // assert
        Model.getPostDao().get(20);
    }

    // ==================================================
    // --> Posts: Delete all
    // ==================================================
    @Test
    public void deleteAllPostTest() throws SQLException, EntityNotFoundException, InvalidArgumentException {
        Model.getPostDao().insert(new Post(10, "Hello World!", "username", 509, 623, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(20, "Hello World!", "xv", 84425, 23520, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(30, "Hello World!", "test", 83, 59, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(40, "Hello World!", "xv", 5473, 625, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(50, "Hello World!", "hello", 946, 682, "10/10/2022 12:12"));

        // delete a post
        Model.getPostDao().deleteAll("xv");

        // assert
        assertEquals(0, Model.getPostDao().getSome("likes", "xv", 10).size());
    }

    // ==================================================
    // --> Posts: Get shares distribution
    // ==================================================
    @Test
    public void getSharesDistributionTest() throws SQLException, EntityNotFoundException {
        Model.getPostDao().insert(new Post(10, "Hello World!", "username", 509, 623, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(20, "Hello World!", "xv", 84425, 23520, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(30, "Hello World!", "test", 83, 59, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(40, "Hello World!", "xv", 5473, 625, "10/10/2022 12:12"));
        Model.getPostDao().insert(new Post(50, "Hello World!", "hello", 946, 682, "10/10/2022 12:12"));

        // get distribution
        assertEquals(new ArrayList<Integer>(Arrays.asList(1, 3, 1)), Model.getPostDao().getSharesDistribution());
    }
}
