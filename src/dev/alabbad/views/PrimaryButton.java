package dev.alabbad.views;

import javafx.scene.control.Button;

/**
 * A button element with primary button styles applied
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class PrimaryButton extends Button {
    public PrimaryButton(String content) {
        this.setText(content);
        this.setMaxWidth(Double.MAX_VALUE);
        this.getStylesheets().add("/css/button.css");
        this.getStyleClass().add("button");
        this.getStyleClass().add("primary");
    }
}
