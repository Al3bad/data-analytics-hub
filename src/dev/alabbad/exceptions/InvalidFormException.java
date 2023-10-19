package dev.alabbad.exceptions;

import java.util.HashMap;

/**
 * Custom exception for invalid forms
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class InvalidFormException extends Exception {
    private HashMap<String, String> errors;

    public InvalidFormException(String msg, HashMap<String, String> errors) {
        super(msg);
        this.errors = errors;
    }

    public HashMap<String, String> getErrors() {
        return errors;
    }
}
