package dev.alabbad.views;

/**
 * A button element with secondary button styles applied
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class SecondaryButton extends PrimaryButton {
    public SecondaryButton(String content) {
        super(content);
        this.getStyleClass().remove("primary");
        this.getStyleClass().add("secondary");
    }
}
