package dev.alabbad.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import dev.alabbad.exceptions.ParseValueException;

/**
 * This class is a library of methods to parse different types of data.
 *
 * @author Abdullah Alabbad
 * @version 2.0.0
 */
public class Parser {
    /**
     * Parse string value. It trims whitespace at the start and the end of the
     * string.
     *
     * @param str string value to be parsed
     * @return parsed string
     * @throws ParseValueException when the parsed string is empty
     */
    public static String parseStr(String str) throws ParseValueException {
        if (str.trim().length() == 0) {
            throw new ParseValueException("Value cannot be empty!");
        }
        return str.trim();
    }

    /**
     * Parse string value. It trims whitespace at the start and the end of the
     * string.
     *
     * @param str        string value to be parsed
     * @param allowSpace
     * @return parsed string
     * @throws ParseValueException when the parsed string is empty OR when
     *                             `allowSpace` is `false` and the parsed string
     *                             contains spaces in the middle
     */
    public static String parseStr(String str, Boolean allowSpace) throws ParseValueException {
        if (allowSpace) {
            return parseStr(str);
        }
        String validStr = parseStr(str);
        if (validStr.split(" ").length > 1) {
            throw new ParseValueException("Value cannot contain spaces!");
        }
        return validStr;
    }

    /**
     * Parse string value. It trims whitespace at the start and the end of the
     * string.
     *
     * @param str        string value to be parsed
     * @param allowSpace
     * @param allowEmpty
     * @return parsed string
     * @throws ParseValueException when the parsed string is empty OR when
     *                             `allowSpace` is `false` and the parsed string
     *                             contains spaces in the middle
     *                             OR when `allowEmpty` is `false` and the parsed
     *                             string is empty
     */
    public static String parseStr(String str, Boolean allowSpace, Boolean allowEmpty) throws ParseValueException {
        if (allowEmpty == true && str.trim().length() == 0) {
            return "";
        }
        return parseStr(str, allowSpace);
    }

    /**
     * Parse password string.
     *
     * @param str password value to be parsed
     * @return parsed password string
     * @throws ParseValueException when `str` length is less than 6 characters
     */
    public static String parsePassword(String str) throws ParseValueException {
        if (str.trim().length() < 6) {
            throw new ParseValueException("Value must have a least 6 charachters!");
        }
        return str.trim();
    }

    /**
     * Parse date/time string.
     *
     * @param dateTime date/time string to be parsed
     * @return Valid date/time as string
     * @throws ParseValueException when the fortmat of the input doesn't match the
     *                             expected format
     */
    public static String parseDateTime(String dateTime) throws ParseValueException {
        String dateTimeFormat = "dd/MM/yyyy HH:mm";
        DateFormat dateFormat = new SimpleDateFormat(dateTimeFormat);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(dateTime);
            return dateTime;
        } catch (ParseException e) {
            throw new ParseValueException(String.format("Value must be in this format: %s", dateTimeFormat));
        }
    }

    /**
     * Parse integer string value.
     *
     * @param integerStr integer string to be parsed
     * @param min        minimum value of integer allowed
     * @return parsed integer
     * @throws ParseValueException when an invalid integer string value is provided
     *                             OR the parsed integer is less the the number
     *                             specified
     */
    public static int parseInt(String integerStr, int min) throws ParseValueException {
        try {
            int validInt = Integer.parseInt(integerStr);
            if (validInt < min) {
                throw new ParseValueException("Value must be a positive integer!");
            }
            return validInt;
        } catch (NumberFormatException e) {
            throw new ParseValueException("Value must be integer!");
        }
    }

    /**
     * Parse integer string value. It return null if the `allowEmpty` is `true` and
     * and empty string was provided.
     *
     * @param integerStr integer string to be parsed
     * @param min        minimum value of integer allowed
     * @param allowEmpty parsed integer
     * @return parsed integer
     * @throws ParseValueException when `allowEmpty` is `false` and an empty string
     *                             is provided OR when an invalid integer string
     *                             value is provided OR the parsed
     *                             integer is less the the number specified
     */
    public static Integer parseInt(String integerStr, int min, Boolean allowEmpty) throws ParseValueException {
        if (allowEmpty == true && integerStr.trim().length() == 0) {
            return null;
        }
        return parseInt(integerStr, min);
    }
}
