package dev.alabbad.views;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class PortalScene extends AnchorPane {
    @FXML
    private VBox container;

    public PortalScene(VBox form) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/portal-scene.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        container.getChildren().add(form);
    }

    public VBox getContainer() {
        return this.container;
    }
}
