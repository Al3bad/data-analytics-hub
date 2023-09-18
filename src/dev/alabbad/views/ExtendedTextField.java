package dev.alabbad.views;

import dev.alabbad.exceptions.ParseValueException;
import javafx.scene.control.TextField;

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

    public T parse() throws ParseValueException {
        this.parsedVal = parsor.run(this.getText());
        return this.parsedVal;
    }
}
