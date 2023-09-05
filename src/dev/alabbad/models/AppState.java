package dev.alabbad.models;

import dev.alabbad.controllers.CPortalScene;
import dev.alabbad.controllers.LoginFormController;
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

    public void switchScene(Scene scene, Boolean loginIsRequired) {
        if (loginIsRequired && this.loggedInUser == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login is required");
            alert.setContentText("Something worng happend! Please contact the developer");
            alert.showAndWait();
            scene = new Scene(new CPortalScene(new LoginFormController()));
        }
        this.stage.setScene(scene);
    }
}
