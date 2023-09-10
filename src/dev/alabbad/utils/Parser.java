package dev.alabbad.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
        if (allowSpace) {
            return parseStr(str);
        }

        String validStr = parseStr(str);
        if (validStr.split(" ").length > 1) {
            throw new Exception("Invalid string! Value cannot have spaces!");
        }
        return validStr;
    }

    public static String parseStr(String str, Boolean allowSpace, Boolean allowEmpty) throws Exception {
        if (allowEmpty == true && str.trim().length() == 0) {
            return "";
        }
        return parseStr(str, allowSpace);
    }

    public static String parsePassword(String str) throws Exception {
        if (str.trim().length() < 6) {
            throw new Exception("Password must have a least 6 charachters!");
        }
        return str.trim();
    }

    /**
     * Parse date/time string
     *
     * @param dateTime date/time string to be parsed
     * @return Valid date/time as string
     * @throws ParseValueException when the fortmat of the input doesn't match the
     * expected format
     */
    public static String parseDateTime(String dateTime) throws Exception {
        String dateTimeFormat = "dd/MM/yyyy HH:mm";
        DateFormat dateFormat = new SimpleDateFormat(dateTimeFormat);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(dateTime);
            return dateTime;
        } catch (Exception e) {
            throw new Exception(String.format("Invalid date/time! Expected format: %s", dateTimeFormat));
        }
    }

    /**
     * Parse integer string value
     *
     * @param integerStr integer string to be parsed
     * @param min minimum value of integer allowed
     * @return Parsed integer
     * @throws ParseValueException when an invalid integer string value is provided
     * or the parsed integer is less the the number specified
     */
    public static int parseInt(String integerStr, int min) throws Exception {
        try {
            int validInt = Integer.parseInt(integerStr);
            if (validInt < min) {
                throw new Exception("Invalid number! Value must be positive integer!");
            }
            return validInt;
        } catch (NumberFormatException e) {
            throw new Exception("Invalid number! Value must be integer");
        }
    }

    public static int parseInt(String integerStr, int min, Boolean allowEmpty) throws Exception {
        if (allowEmpty == true && integerStr.trim().length() == 0) {
            return -1;
        }
        return parseInt(integerStr, min);
    }
}
