package dev.alabbad.exceptions;

/**
 * Custom exception for thrown when attempting to perform an unauthorised action
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class UnauthorisedAction extends Exception {
    public UnauthorisedAction(String msg) {
        super(msg);
    }
}
