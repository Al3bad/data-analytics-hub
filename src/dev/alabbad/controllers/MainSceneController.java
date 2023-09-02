package dev.alabbad.controllers;

import dev.alabbad.models.AppState;
import dev.alabbad.models.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
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

    private HashMap<Toggle, VBox> tapMap = new HashMap<>();

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

        this.tapMap.put(this.dashboardTab, new VBox(new Text("Dashboard")));
        this.tapMap.put(this.addPostTab, new VBox(new Text("Add Post")));
        this.tapMap.put(this.deletePostTab, new VBox(new Text("Delete Post")));
        this.tapMap.put(this.getPostTab, new VBox(new Text("get Post")));
        this.tapMap.put(this.getMostLikedPostsTab, new VBox(new Text("Get Most Liked Posts")));
        this.tapMap.put(this.getMostSharedPostsTab, new VBox(new Text("Get Most Shared Posts")));
        this.tapMap.put(this.editProfileTab, new EditProfileFormController());

        displaySelectedPane();
        this.actionsGroup.selectedToggleProperty()
                        .addListener((observable, oldValue, newValue) -> displaySelectedPane());

        // Setup
        User user = AppState.getInstance().getUser();
        if (user != null) {
            this.fullName.setText("Hello " + user.getFirstName() + " " + user.getLastName());
            this.username.setText("@" + user.getUsername());
        }
    }

    private void displaySelectedPane() {
        this.container.getChildren().setAll(this.tapMap.get(this.actionsGroup.getSelectedToggle()));
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
    }

    @FXML
    public void onPostTabClicked(MouseEvent event) {
        System.out.println("Posts tab clicked!");
    }
}
