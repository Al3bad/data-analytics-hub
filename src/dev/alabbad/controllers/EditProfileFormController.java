package dev.alabbad.controllers;

import dev.alabbad.exceptions.UserNotFoundException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class EditProfileFormController extends SignupFormController {
    public EditProfileFormController() {
        super();
        this.textFieldElements.put("New Password", new PasswordField());
        setupForm();
        // TODO: make sure that user creds are valid before updating
        // TODO: handle some errors properly

        User loggedinUser = AppState.getInstance().getUser();
        this.textFieldElements.get("Username").setText(loggedinUser.getUsername());
        this.textFieldElements.get("First Name").setText(loggedinUser.getFirstName());
        this.textFieldElements.get("Last Name").setText(loggedinUser.getLastName());
        this.textFieldElements.get("Password").setText("");
        // Replace the label from "Password" to "Current Password"
        VBox inputContainer = (VBox) this.textFieldElements.get("Password").getParent();
        Label label = (Label) inputContainer.getChildren().get(0);
        label.setText("Current Password");
        this.primaryBtn.setText("Edit");
    }

    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        User userDetails = this.validateForm();

        if (userDetails == null) {
            return false;
        }

        try {
            String currentUsername = AppState.getInstance().getUser().getUsername();
            User updatedUser = DB.updateUser(currentUsername, userDetails.getUsername(), userDetails.getPassword(),
                            userDetails.getFirstName(), userDetails.getLastName());
            if (updatedUser == null) {
                this.afterContainer.getChildren().setAll(new CAlert("Something wrong happend!", "error"));
                return false;
            } else {
                this.afterContainer.getChildren().setAll(new CAlert("User has been successfully created!", "success"));
                AppState.getInstance().setUser(updatedUser);
                Scene dashboardScene = new Scene(new MainSceneController());
                AppState.getInstance().switchScene(dashboardScene, true);
            }
        } catch (UserNotFoundException e) {
            this.afterContainer.getChildren().setAll(new CAlert("User is not found!", "error"));
            return false;
        }
        return true;
    }

    @Override
    protected void onSecondaryBtnClicked(MouseEvent event) {
    }
}
