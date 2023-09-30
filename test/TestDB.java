package test;

import org.junit.*;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import dev.alabbad.models.DB;

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
        assertFalse(DB.createPostTable());
    }

    // ==================================================
    // --> Users operations
    // ==================================================
}
