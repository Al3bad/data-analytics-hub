package dev.alabbad.exceptions;

/**
 * Custom exception for not found eneities
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String msg) {
        super(msg);
    }
}
