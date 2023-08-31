package dev.alabbad.models;

import dev.alabbad.controllers.CLoginForm;
import dev.alabbad.controllers.CPortalScene;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class AppState {
    private static AppState instance;
    User loggedInUser;
    Stage stage;

    private AppState() {
    }

    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    public User getUser() {
        return this.loggedInUser;
    }

    public void setUser(User user) {
        this.loggedInUser = user;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void switchScene(Scene scene, double minWidth, double minHeight, Boolean loginIsRequired) {
        if (loginIsRequired && this.loggedInUser == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login is required");
            alert.setContentText("Something worng happend! Please contact the developer");
            alert.showAndWait();
            minWidth = 650;
            minHeight = 400;
            scene = new Scene(new CPortalScene(), minWidth, minHeight);
        }
        this.stage.setScene(scene);
        this.stage.setMinWidth(minWidth);
        this.stage.setMinHeight(minHeight);
    }
}
