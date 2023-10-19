package dev.alabbad.views;

import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * An alert view that displayes information to the user
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class AlertView extends HBox {
    private Text content;

    public AlertView(String content, String type) {
        // set text
        this.content = new Text(content);

        // add styles
        String baseStyle = "-fx-padding: 12; -fx-border-width: 1; -fx-border-radius: 4; ";
        if (type == "success") {
            // Success Color
            this.setStyle(baseStyle + "-fx-border-color: #8CAB8F80; -fx-background-color: #B2D8BF80;");
            this.content.setFill(Color.web("#659575"));
        } else if (type == "error") {
            // Error Color
            this.setStyle(baseStyle + "-fx-border-color: #AB908C80; -fx-background-color: #D8B7B280;");
            this.content.setFill(Color.web("#AC756C"));
        } else if (type == "info") {
            // Neutral Color
            this.setStyle(baseStyle + "-fx-border-color: #8CA1AB80; -fx-background-color: #B2CCD880;");
            this.content.setFill(Color.web("#6C97AC"));
        } else {
            // Neutral Color
            this.setStyle(baseStyle + "-fx-border-color: #8CA1AB80; -fx-background-color: #B2CCD880;");
            this.content.setFill(Color.web("#6C97AC"));
        }

        this.getChildren().setAll(this.content);
    }
}
