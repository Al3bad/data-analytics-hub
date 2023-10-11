package dev.alabbad.models;

import dev.alabbad.controllers.LoginFormController;
import dev.alabbad.views.PortalScene;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * AppState singletone. It holds the logged in user object, the stage of the
 * app, and a method to switch scenes.
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class AppState {
    private static AppState instance;
    User loggedInUser;
    Stage stage;

    // prevent object instantiation from outside
    private AppState() {
    }

    // lazy instantiation
    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    // getters
    public User getUser() {
        return this.loggedInUser;
    }

    public Stage getStage() {
        return this.stage;
    }

    // setters
    public void setUser(User user) {
        this.loggedInUser = user;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Switch scenes in the global stage
     *
     * @param scene           new scene to be switched to
     * @param loginIsRequired flag
     */
    public void switchScene(Scene scene, Boolean loginIsRequired) {
        // switch to login scene when the used is not logged in and tries to navigate
        // to a restricted view
        if (loginIsRequired && this.loggedInUser == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login is required");
            alert.setContentText("Something worng happend! Please contact the developer");
            alert.showAndWait();
            scene = new Scene(new PortalScene(new LoginFormController()));
        }
        // set scene
        this.stage.setWidth(this.stage.getWidth());
        this.stage.setHeight(this.stage.getHeight());
        this.stage.setScene(scene);
    }
}
