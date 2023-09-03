package dev.alabbad.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CAlert extends HBox {
    @FXML
    private HBox container;

    @FXML
    private Text content;

    public CAlert(String content, String type) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/alert.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        setupElements(content, type);
    }

    private void setupElements(String content, String type) {

        String baseStyle = "-fx-border-width: 1; -fx-border-radius: 4; ";
        if (type == "success") {
            // Success Color
            // -fx-border-color: #8CAB8F80;
            // -fx-background-color: #B2D8BF80;
            // text color: #659575
            this.container.setStyle(baseStyle + "-fx-border-color: #8CAB8F80; -fx-background-color: #B2D8BF80;");
            this.content.setFill(Color.web("#659575"));
        } else if (type == "error") {
            // Error Color
            // -fx-border-color: #AB908C80;
            // -fx-background-color: #D8B7B280;
            // text color: #AC756C
            this.container.setStyle(baseStyle + "-fx-border-color: #AB908C80; -fx-background-color: #D8B7B280;");
            this.content.setFill(Color.web("#AC756C"));
        } else if (type == "info") {
            // Neutral Color
            // -fx-border-color: #8CA1AB80;
            // -fx-background-color: #B2CCD880;
            // text color: #6C97AC
            this.container.setStyle(baseStyle + "-fx-border-color: #8CA1AB80; -fx-background-color: #B2CCD880;");
            this.content.setFill(Color.web("#6C97AC"));
        } else {
            // Neutral Color
            // -fx-border-color: #8CA1AB80;
            // -fx-background-color: #B2CCD880;
            // text color: #6C97AC
            this.container.setStyle(baseStyle + "-fx-border-color: #8CA1AB80; -fx-background-color: #B2CCD880;");
            this.content.setFill(Color.web("#6C97AC"));
        }

        this.content.setText(content);
    }
}
