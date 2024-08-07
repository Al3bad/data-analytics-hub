package dev.alabbad.views;

import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;

/**
 * PasswordField with parser handler
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class ExtendedPasswordField<T> extends ExtendedTextField<T> {
    public ExtendedPasswordField(Function<T> parsor) {
        super(parsor);
        this.setSkin(new PasswordFieldSkin(this));
    }

    // reference:
    // - https://www.youtube.com/watch?v=YAei6rpMzkc
    // - Changing the skin of the text field
    private class PasswordFieldSkin extends TextFieldSkin {
        static final char BULLET = '\u25cf';

        public PasswordFieldSkin(TextField textField) {
            super(textField);
        }

        /** {@inheritDoc} */
        @Override
        protected String maskText(String txt) {
            if (getSkinnable() instanceof TextField) {
                int n = txt.length();
                StringBuilder passwordBuilder = new StringBuilder(n);
                for (int i = 0; i < n; i++) {
                    passwordBuilder.append(BULLET);
                }

                return passwordBuilder.toString();
            } else {
                return txt;
            }
        }
    }
}
