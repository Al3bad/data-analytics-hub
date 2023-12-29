package dev.alabbad.views;

import dev.alabbad.exceptions.ParseValueException;
import dev.alabbad.interfaces.IInputControl;
import javafx.scene.control.TextField;

/**
 * TextFieled with parser handler
 *
 * @author Abdullah Alabbad
 * @version 1.0.1
 */
public class ExtendedTextField<T> extends TextField implements IInputControl<T> {
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
