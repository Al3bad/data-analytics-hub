package dev.alabbad.controllers;

import dev.alabbad.models.AppState;
import dev.alabbad.models.User;

import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class DashboardSceneController extends AnchorPane {
    @FXML
    private Text fullName;

    @FXML
    private Text username;

    @FXML
    private VBox container;

    @FXML
    private Button dashboardTab;

    @FXML
    private Button postsTab;

    private String activeTab;

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

        activeTab = "dashboard";
        this.dashboardTab.getStyleClass().add("active");

        // Setup
        User user = AppState.getInstance().getUser();
        if (user != null) {
            this.fullName.setText("Hello " + user.getFirstName() + " " + user.getLastName());
            this.username.setText("@" + user.getUsername());
        }
    }

    @FXML
    public void onEditBtnClicked(MouseEvent event) {
        AppState.getInstance().switchScene(new Scene(new EditProfileFormController()), true);
    }

    @FXML
    public void onLogoutBtnClicked(MouseEvent event) {
        // create and show logout conformation dialog
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.setTitle("Logout Conformation");
        dialog.setContentText("Are you sure you want to logout?");
        ButtonType logoutBtn = new ButtonType("Logout", ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(logoutBtn);
        dialog.getDialogPane().getButtonTypes().add(cancelBtn);
        Optional<ButtonType> result = dialog.showAndWait();

        // take action based on result
        if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
            AppState.getInstance().setUser(null);
            AppState.getInstance().switchScene(new Scene(new CPortalScene(new LoginFormController())), false);
        }
    }

    @FXML
    public void onDashboardTabClicked(MouseEvent event) {
        System.out.println("Dashboard tab clicked!");
        if (activeTab != "dashboard") {
            activeTab = "dashboard";
            this.dashboardTab.getStyleClass().add("active");
            this.postsTab.getStyleClass().remove("active");
        }
    }

    @FXML
    public void onPostTabClicked(MouseEvent event) {
        System.out.println("Posts tab clicked!");
        if (activeTab != "posts") {
            activeTab = "posts";
            this.dashboardTab.getStyleClass().remove("active");
            this.postsTab.getStyleClass().add("active");
        }
    }
}
