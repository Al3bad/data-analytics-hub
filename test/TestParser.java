package test;

import org.junit.*;
import static org.junit.Assert.*;

import dev.alabbad.exceptions.ParseValueException;
import dev.alabbad.utils.Parser;

public class TestParser {
    // ===============================================
    // --> parseStr()
    // ===============================================
    @Test
    public void parseStrTest() throws ParseValueException {
        assertEquals("Hello World!", Parser.parseStr("Hello World!  "));
    }

    @Test
    public void parseStrTest_allowSpace() throws ParseValueException {
        // test allowSpace
        assertEquals("Hello World!", Parser.parseStr("Hello World!  ", true));
        assertEquals("HelloWorld!", Parser.parseStr("HelloWorld!", false));
    }

    @Test
    public void parseStrTest_allowEmpty() throws ParseValueException {
        // test allowEmpty
        assertNull(Parser.parseStr("", false, true));
    }

    @Test(expected = ParseValueException.class)
    public void parseStrTest_emptyString() throws ParseValueException {
        Parser.parseStr("   ");
    }

    @Test(expected = ParseValueException.class)
    public void parseStrTest_containsSpaces() throws ParseValueException {
        Parser.parseStr("Hello World", false);
    }

    @Test(expected = ParseValueException.class)
    public void parseStrTest_emptyStringBetweenWords() throws ParseValueException {
        Parser.parseStr("Hello World", false, false);
    }

    // ===============================================
    // --> parsePassword()
    // ===============================================
    @Test
    public void parsePasswordTest() throws ParseValueException {
        assertEquals("pass  ", Parser.parsePassword("pass  "));
    }

    @Test(expected = ParseValueException.class)
    public void parsePasswordTest_invalidLength() throws ParseValueException {
        Parser.parsePassword("123");
    }

    // ===============================================
    // --> parseInt()
    // ===============================================
    @Test
    public void parseIntTest() throws ParseValueException {
        assertEquals(123, Parser.parseInt("123", 0));
    }

    @Test
    public void prseIntTest_allowEmpty() throws ParseValueException {
        assertNull(Parser.parseInt("", 0, true));
    }

    @Test(expected = ParseValueException.class)
    public void parseIntTest_ParseValueExcpetion_EmptyStr() throws ParseValueException {
        Parser.parseInt("", 0);
    }

    @Test(expected = ParseValueException.class)
    public void parseIntTest_ParseValueExcpetion_InvalidNum() throws ParseValueException {
        Parser.parseInt("12g", 0);
    }

    @Test(expected = ParseValueException.class)
    public void parseIntTest_ParseValueExcpetion_OutOfRnageNeg() throws ParseValueException {
        Parser.parseInt("-12", 0);
    }

    // ===============================================
    // --> parseDate()
    // ===============================================
    @Test
    public void parseDateTest_Valid() throws ParseValueException {
        String dateTime = Parser.parseDateTime("12/05/2023 10:10");
        assertEquals("12/05/2023 10:10", dateTime);
    }

    @Test(expected = ParseValueException.class)
    public void parseDateTest_ParseValueException() throws ParseValueException {
        Parser.parseDateTime("1a/05/2023 10:10");
    }

    @Test(expected = ParseValueException.class)
    public void parseDateTest_ParseValueException_Format() throws ParseValueException {
        Parser.parseDateTime("12-05-2023 10:10");
    }

    @Test(expected = ParseValueException.class)
    public void parseDateTest_ParseValueException_OutOfRange() throws ParseValueException {
        Parser.parseDateTime("40/05/2023 10:10");
    }
}
