package dev.alabbad.controllers;

import dev.alabbad.exceptions.UserNotFoundException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public class EditProfileFormController extends SignupFormController {
    public EditProfileFormController() {
        super();
        // TODO: add field for old & new password
        // TODO: make sure that user creds are valid before updating
        // TODO: handle some errors properly

        this.secondryBtn.setVisible(false);

        User loggedinUser = AppState.getInstance().getUser();
        this.username.setText(loggedinUser.getUsername());
        this.password.setText("");
        this.fname.setText(loggedinUser.getFirstName());
        this.lname.setText(loggedinUser.getLastName());
        this.primaryBtn.setText("Edit");
        this.secondryBtn.setText("Cancel");
    }

    @Override
    protected Boolean onSubmitBtnClicked(MouseEvent event) {
        User userDetails = this.validateForm();

        if (userDetails == null) {
            return false;
        }

        try {
            String currentUsername = AppState.getInstance().getUser().getUsername();
            User updatedUser = DB.updateUser(currentUsername, userDetails.getUsername(), userDetails.getPassword(),
                            userDetails.getFirstName(), userDetails.getLastName());
            if (updatedUser == null) {
                this.statusContainer.getChildren().setAll(new CAlert("Something wrong happend!", "error"));
                return false;
            } else {
                this.statusContainer.getChildren().setAll(new CAlert("User has been successfully created!", "success"));
                AppState.getInstance().setUser(updatedUser);
                Scene dashboardScene = new Scene(new MainSceneController());
                AppState.getInstance().switchScene(dashboardScene, true);
            }
        } catch (UserNotFoundException e) {
            this.statusContainer.getChildren().setAll(new CAlert("User is not found!", "error"));
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelBtnClicked(MouseEvent event) {
        // Scene dashboardScene = new Scene(new DashboardSceneController());
        // AppState.getInstance().switchScene(dashboardScene, true);
    }
}
