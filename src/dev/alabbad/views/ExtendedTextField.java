package dev.alabbad.views;

import dev.alabbad.exceptions.ParseValueException;
import javafx.scene.control.TextField;

/**
 * TextFiled with parser handler
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class ExtendedTextField<T> extends TextField {
    // reference:
    // -
    // https://www.w3docs.com/snippets/java/how-to-pass-a-function-as-a-parameter-in-java.html
    @FunctionalInterface
    public interface Function<T> {
        T run(String val) throws ParseValueException;
    }

    private Function<T> parsor;
    private T parsedVal;

    public ExtendedTextField(Function<T> parsor) {
        this.parsor = parsor;
    }

    public T getParsedVal() {
        return parsedVal;
    }

    /**
     * Parse the value from the text field using the specified parser
     *
     * @return parsed value
     * @throws ParseValueException when failed to parse
     */
    public T parse() throws ParseValueException {
        this.parsedVal = parsor.run(this.getText());
        return this.parsedVal;
    }
}
