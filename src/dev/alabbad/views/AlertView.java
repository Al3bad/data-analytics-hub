package dev.alabbad.views;

import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class AlertView extends HBox {
    private Text content;

    public AlertView(String content, String type) {
        this.content = new Text(content);
        this.getChildren().setAll(this.content);
        setupElements(content, type);
    }

    private void setupElements(String content, String type) {
        String baseStyle = "-fx-padding: 12; -fx-border-width: 1; -fx-border-radius: 4; ";
        if (type == "success") {
            // Success Color
            // -fx-border-color: #8CAB8F80;
            // -fx-background-color: #B2D8BF80;
            // text color: #659575
            this.setStyle(baseStyle + "-fx-border-color: #8CAB8F80; -fx-background-color: #B2D8BF80;");
            this.content.setFill(Color.web("#659575"));
        } else if (type == "error") {
            // Error Color
            // -fx-border-color: #AB908C80;
            // -fx-background-color: #D8B7B280;
            // text color: #AC756C
            this.setStyle(baseStyle + "-fx-border-color: #AB908C80; -fx-background-color: #D8B7B280;");
            this.content.setFill(Color.web("#AC756C"));
        } else if (type == "info") {
            // Neutral Color
            // -fx-border-color: #8CA1AB80;
            // -fx-background-color: #B2CCD880;
            // text color: #6C97AC
            this.setStyle(baseStyle + "-fx-border-color: #8CA1AB80; -fx-background-color: #B2CCD880;");
            this.content.setFill(Color.web("#6C97AC"));
        } else {
            // Neutral Color
            // -fx-border-color: #8CA1AB80;
            // -fx-background-color: #B2CCD880;
            // text color: #6C97AC
            this.setStyle(baseStyle + "-fx-border-color: #8CA1AB80; -fx-background-color: #B2CCD880;");
            this.content.setFill(Color.web("#6C97AC"));
        }

        this.content.setText(content);
    }
}
