package dev.alabbad.exceptions;

import java.util.ArrayList;

/**
 * Custom exception for invalid arguemnt of methods and program
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class InvalidArgumentException extends Exception {
    ArrayList<String> args;

    public InvalidArgumentException(String msg) {
        super(msg);
    }

    public InvalidArgumentException(String msg, ArrayList<String> args) {
        super(msg);
        this.args = args;
    }
}
