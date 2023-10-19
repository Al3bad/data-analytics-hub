package dev.alabbad.views;

import java.io.IOException;
import java.util.Set;

import dev.alabbad.controllers.AvatarController;
import dev.alabbad.controllers.DashboardController;
import dev.alabbad.controllers.DeletePostFormController;
import dev.alabbad.controllers.EditProfileFormController;
import dev.alabbad.controllers.GetMostLikedPostsFormController;
import dev.alabbad.controllers.GetMostSharedPostsFormController;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Main scene
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
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
    private VBox tabs;

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
        // load fxml file
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/main-scene.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        User user = AppState.getInstance().getUser();

        // add Users tab when the the logged in user is admin
        if (user instanceof AdminUser) {
            this.addUsersTab();
        }

        this.initTabs();
        this.initUserDetails(user);
        this.displaySelectedTab();
    }

    /**
     * Add users tab with the necessary styles
     */
    private void addUsersTab() {
        this.usersTab = new RadioButton();
        this.usersTab.setText("Users");
        this.usersTab.getStylesheets().add("/css/tab.css");
        this.usersTab.getStyleClass().add("tab");
        this.usersTab.getStyleClass().add("tab-container");
        this.usersTab.toggleGroupProperty().set(this.actionsGroup);
        this.tabs.getChildren().add(new HBox(usersTab));
    }

    /**
     * Initialise tabs
     */
    private void initTabs() {
        // get all radio buttons (act as tabs)
        Set<Node> radioButtons = this.lookupAll(".tab");

        // remove the default styles of the radio buttons (act as tabs)
        for (Node textField : radioButtons) {
            ((RadioButton) textField).getStyleClass().remove("radio-button");
        }

        // add event add event listener to the radio buttons (act as tabs)
        this.actionsGroup.selectedToggleProperty()
                        .addListener((observable, oldValue, newValue) -> displaySelectedTab());
    }

    /**
     * Add the avatar and fill in details of the logged in user
     *
     * @param user logged in user
     */
    private void initUserDetails(User user) {
        this.userDetailsContainer.getChildren().add(0, new AvatarController());
        this.fullName.setText("Hello " + user.getFirstName() + " " + user.getLastName());
        this.username.setText("@" + user.getUsername());
    }

    /**
     * Disaplay the element of the the selcted tab (radio button) in the main
     * container
     */
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
            this.container.getChildren().setAll(new GetMostLikedPostsFormController());
        } else if (this.actionsGroup.getSelectedToggle() == this.getMostSharedPostsTab) {
            this.container.getChildren().setAll(new GetMostSharedPostsFormController());
        } else if (this.actionsGroup.getSelectedToggle() == this.editProfileTab) {
            this.container.getChildren().setAll(new EditProfileFormController());
        } else if (this.actionsGroup.getSelectedToggle() == this.usersTab) {
            this.container.getChildren().setAll(new UsersController());
        }
    }

    /**
     * Handler of the logout button
     *
     * @param event moust event
     */
    @FXML
    public void onLogoutBtnClicked(MouseEvent event) {
        // create and show logout conformation dialog
        new DialogView("Logout Conformation", "Are you sure you want to logout?", "Logout", "Cancel", () -> {
            AppState.getInstance().setUser(null);
            AppState.getInstance().switchScene(new Scene(new PortalScene(new LoginFormController())), false);
        });
    }
}
