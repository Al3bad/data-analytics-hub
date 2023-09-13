package dev.alabbad.controllers;

import dev.alabbad.models.AppState;
import dev.alabbad.models.User;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MainSceneController extends AnchorPane {
    // User details section
    @FXML
    private Text fullName;

    @FXML
    private Text username;

    // Actions items
    @FXML
    private VBox tabs; // NOTE: we might need this when implementing stage 2

    @FXML
    private ToggleGroup actionsGroup;

    @FXML
    private RadioButton dashboardTab;

    @FXML
    private RadioButton addPostTab;

    @FXML
    private RadioButton deletePostTab;

    @FXML
    private RadioButton getPostTab;

    @FXML
    private RadioButton getMostLikedPostsTab;

    @FXML
    private RadioButton getMostSharedPostsTab;

    @FXML
    private RadioButton editProfileTab;

    // Main content
    @FXML
    private VBox container;

    public MainSceneController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/main-scene.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            System.out.println("Something wrong happends while load dashboard scene!!!");
            throw new RuntimeException(exception);
        }

        // get all TextField in the form
        Set<Node> radioButtons = this.lookupAll(".tab");
        // put TextField element in the HashMap
        for (Node textField : radioButtons) {
            ((RadioButton) textField).getStyleClass().remove("radio-button");
        }

        displaySelectedTab();
        this.actionsGroup.selectedToggleProperty()
                        .addListener((observable, oldValue, newValue) -> displaySelectedTab());

        User user = AppState.getInstance().getUser();
        if (user != null) {
            this.fullName.setText("Hello " + user.getFirstName() + " " + user.getLastName());
            this.username.setText("@" + user.getUsername());
        }
    }

    private void displaySelectedTab() {
        if (this.actionsGroup.getSelectedToggle() == this.dashboardTab) {
            this.container.getChildren().setAll(new DashboardController());
        } else if (this.actionsGroup.getSelectedToggle() == this.addPostTab) {
            this.container.getChildren().setAll(new NewPostFormController());
        } else if (this.actionsGroup.getSelectedToggle() == this.deletePostTab) {
            this.container.getChildren().setAll(new DeletePostFormController());
        } else if (this.actionsGroup.getSelectedToggle() == this.getPostTab) {
            this.container.getChildren().setAll(new GetPostFormController());
        } else if (this.actionsGroup.getSelectedToggle() == this.getMostLikedPostsTab) {
            this.container.getChildren().setAll(new GetMostLikedPostsController());
        } else if (this.actionsGroup.getSelectedToggle() == this.getMostSharedPostsTab) {
            this.container.getChildren().setAll(new GetMostSharedPostsController());
        } else if (this.actionsGroup.getSelectedToggle() == this.editProfileTab) {
            this.container.getChildren().setAll(new EditProfileFormController());
        }
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
}
