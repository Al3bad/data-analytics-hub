package dev.alabbad.models;

import dev.alabbad.controllers.LoginFormController;
import dev.alabbad.views.PortalScene;
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

    public Stage getStage() {
        return this.stage;
    }

    public void setUser(User user) {
        this.loggedInUser = user;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void switchScene(Scene scene, Boolean loginIsRequired) {
        double minWidth = 680;
        double minHeight = 480;
        if (loginIsRequired && this.loggedInUser == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login is required");
            alert.setContentText("Something worng happend! Please contact the developer");
            alert.showAndWait();
            scene = new Scene(new PortalScene(new LoginFormController()), minWidth, minHeight);
        }

        this.stage.setWidth(minWidth);
        this.stage.setHeight(minHeight);
        this.stage.setScene(scene);
    }
}
