package dev.alabbad.views;

import javafx.scene.control.TextField;

public class ExtendedTextField<T> extends TextField {
    // reference:
    // -
    // https://www.w3docs.com/snippets/java/how-to-pass-a-function-as-a-parameter-in-java.html
    @FunctionalInterface
    public interface Function<T> {
        T run(String val) throws Exception;
    }

    private Function<T> parsor;
    private T parsedVal;

    public ExtendedTextField(Function<T> parsor) {
        this.parsor = parsor;
    }

    public T getParsedVal() {
        return parsedVal;
    }

    public T parse() throws Exception {
        this.parsedVal = parsor.run(this.getText());
        return this.parsedVal;
    }
}
