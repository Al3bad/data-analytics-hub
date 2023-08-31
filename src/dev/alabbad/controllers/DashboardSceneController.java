package dev.alabbad.controllers;

import dev.alabbad.models.AppState;
import dev.alabbad.models.User;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class DashboardSceneController extends AnchorPane {
    @FXML
    private AnchorPane component;

    @FXML
    private Text fullName;

    @FXML
    private Text username;

    @FXML
    private Text numOfPosts;

    @FXML
    private ListView<String> postContainer;

    public DashboardSceneController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/dashboard-scene.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            System.out.println("Something wrong happends while load dashboard scene!!!");
            throw new RuntimeException(exception);
        }
        // Setup
        User user = AppState.getInstance().getUser();
        if (user != null) {
            this.fullName.setText(user.getFirstName() + " " + user.getLastName());
            this.username.setText(user.getUsername());
            this.numOfPosts.setText("0");
        }
    }

    public AnchorPane getComponent() {
        return this.component;
    }
}
