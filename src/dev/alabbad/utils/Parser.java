package dev.alabbad.utils;

public class Parser {
    /**
     * Parse string value. It trims whitespace at the start and the end of the
     * string
     *
     * @param str string value to be parsed
     * @return Parsed string
     * @throws Exception when the parsed string is empty
     */
    public static String parseStr(String str) throws Exception {
        if (str.trim().length() == 0) {
            throw new Exception("Invalid string! Value cannot be empty!");
        }
        return str.trim();
    }

    public static String parseStr(String str, Boolean allowSpace) throws Exception {
        String validStr = parseStr(str);
        if (validStr.split(" ").length > 1) {
            throw new Exception("Invalid string! Value cannot have spaces!");
        }
        return validStr;
    }

    public static String parsePassword(String str) throws Exception {
        if (str.trim().length() < 6) {
            throw new Exception("Password must have a least 6 charachters!");
        }
        return str.trim();
    }

}
