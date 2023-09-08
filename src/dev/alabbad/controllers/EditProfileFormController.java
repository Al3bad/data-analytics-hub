package dev.alabbad.controllers;

import dev.alabbad.elements.ExtendedPasswordField;
import dev.alabbad.exceptions.UserNotFoundException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class EditProfileFormController extends SignupFormController {
    // TextField IDs & Labels
    protected final static String CURRENT_PASSWORD = "Current Password";
    protected final static String NEW_PASSWORD = "New Password";

    public EditProfileFormController() {
        super();
        this.textFieldElements.put(NEW_PASSWORD,
                        new ExtendedPasswordField<String>((val) -> Parser.parseStr(val, false)));
        setupForm();
        // TODO: make sure that user creds are valid before updating
        // TODO: handle some errors properly

        User loggedinUser = AppState.getInstance().getUser();
        this.textFieldElements.get(USERNAME).setText(loggedinUser.getUsername());
        this.textFieldElements.get(FNAME).setText(loggedinUser.getFirstName());
        this.textFieldElements.get(LNAME).setText(loggedinUser.getLastName());
        this.textFieldElements.get(PASSWORD).setText("");
        // Replace the label from "Password" to "Current Password"
        VBox inputContainer = (VBox) this.textFieldElements.get(PASSWORD).getParent();
        Label label = (Label) inputContainer.getChildren().get(0);
        label.setText(CURRENT_PASSWORD);
        this.primaryBtn.setText("Edit");
    }

    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.beforeContainer) == false) {
            return false;
        }
        String username = (String) this.textFieldElements.get(USERNAME).getParsedVal();
        String currentPassword = (String) this.textFieldElements.get(CURRENT_PASSWORD).getParsedVal();
        String newPassword = (String) this.textFieldElements.get(NEW_PASSWORD).getParsedVal();
        String fname = (String) this.textFieldElements.get(FNAME).getParsedVal();
        String lname = (String) this.textFieldElements.get(LNAME).getParsedVal();

        try {
            String currentUsername = AppState.getInstance().getUser().getUsername();
            User updatedUser = DB.updateUser(currentUsername, username, currentPassword, fname, lname);
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
}
