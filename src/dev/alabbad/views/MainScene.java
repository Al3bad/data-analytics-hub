package dev.alabbad.views;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import dev.alabbad.controllers.AvatarController;
import dev.alabbad.controllers.DashboardController;
import dev.alabbad.controllers.DeletePostFormController;
import dev.alabbad.controllers.EditProfileFormController;
import dev.alabbad.controllers.GetMostLikedPostsController;
import dev.alabbad.controllers.GetMostSharedPostsController;
import dev.alabbad.controllers.GetPostFormController;
import dev.alabbad.controllers.LoginFormController;
import dev.alabbad.controllers.NewPostFormController;
import dev.alabbad.controllers.UsersController;
import dev.alabbad.models.AdminUser;
import dev.alabbad.models.AppState;
import dev.alabbad.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MainScene extends AnchorPane {
    // User details section
    @FXML
    private HBox userDetailsContainer;

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
    private RadioButton usersTab;

    @FXML
    private RadioButton editProfileTab;

    // Main content
    @FXML
    private VBox container;

    public MainScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/main-scene.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            System.out.println("Something wrong happends while load dashboard scene!!!");
            throw new RuntimeException(exception);
        }

        // NOTE: List of users for ADMIN USER
        if (AppState.getInstance().getUser() instanceof AdminUser) {
            this.usersTab = new RadioButton();
            this.usersTab.setText("Users");
            this.usersTab.getStylesheets().add("/css/tab.css");
            this.usersTab.getStyleClass().add("tab");
            this.usersTab.getStyleClass().add("tab-container");
            this.usersTab.toggleGroupProperty().set(this.actionsGroup);
            this.tabs.getChildren().add(new HBox(usersTab));
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

        this.userDetailsContainer.getChildren().add(0, new AvatarController());
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
        } else if (this.actionsGroup.getSelectedToggle() == this.usersTab) {
            this.container.getChildren().setAll(new UsersController());
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
            AppState.getInstance().switchScene(new Scene(new PortalScene(new LoginFormController())), false);
        }
    }
}
