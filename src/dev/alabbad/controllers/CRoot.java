package dev.alabbad.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class CRoot extends AnchorPane {
    private AnchorPane component;

    @FXML
    private VBox main;

    public CRoot() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/root.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            this.component = fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        setupElements();
    }

    private void setupElements() {
        // Bind events to event handlers
        // main.getChildren().add(new CCreateUserForm().getComponent());
        // main.getChildren().add(new CLoginUserForm().getComponent());
    }

    public Parent getComponent() {
        return this.component;
    }
}
