package dev.alabbad.interfaces;

import dev.alabbad.exceptions.ParseValueException;

/**
 * The common interface for all custom input controls
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public interface IInputControl<T> {
    // reference:
    // -
    // https://www.w3docs.com/snippets/java/how-to-pass-a-function-as-a-parameter-in-java.html
    @FunctionalInterface
    public interface Function<T> {
        T run(String val) throws ParseValueException;
    }

    public T getParsedVal();

    /**
     * Parse the value from the input control using the specified parser
     *
     * @return parsed value
     * @throws ParseValueException when failed to parse
     */
    public T parse() throws ParseValueException;
}
