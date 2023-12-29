package dev.alabbad.controllers;

import java.sql.SQLException;

import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.interfaces.IInputControl;
import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.Model;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;
import dev.alabbad.views.AlertView;
import dev.alabbad.views.ExtendedPasswordField;
import dev.alabbad.views.ExtendedTextField;
import dev.alabbad.views.MainScene;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

/**
 * Implementation of edit profile form
 *
 * @author Abdullah Alabbad
 * @version 1.0.1
 */
public class EditProfileFormController extends SignupFormController {
    // TextField IDs & Labels
    protected final static String CURRENT_PASSWORD = "Current Password";
    protected final static String NEW_PASSWORD = "New Password";

    public EditProfileFormController() {
        super();
        this.setAlignment(Pos.CENTER);
        this.inputControlElements.remove(PASSWORD);
        this.inputControlElements.put(CURRENT_PASSWORD,
                new ExtendedPasswordField<String>((val) -> Parser.parseStr(val, true)));
        this.inputControlElements.put(NEW_PASSWORD,
                new ExtendedPasswordField<String>((val) -> Parser.parsePassword(val, true)));
        this.setupForm();
        this.fillinForm();
    }

    /**
     * Fill in the form with the details of the loagged in user
     */
    private void fillinForm() {
        User loggedinUser = AppState.getInstance().getUser();
        ((ExtendedTextField) this.inputControlElements.get(USERNAME)).setText(loggedinUser.getUsername());
        ((ExtendedTextField) this.inputControlElements.get(FNAME)).setText(loggedinUser.getFirstName());
        ((ExtendedTextField) this.inputControlElements.get(LNAME)).setText(loggedinUser.getLastName());
        this.primaryBtn.setText("Edit");
    }

    /**
     * Primary button handler - edit user's profile
     *
     * @param event mouse evnet
     */
    @Override
    protected void onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.beforeContainer) == false) {
            return;
        }

        // get values from text fields
        String username = AppState.getInstance().getUser().getUsername();
        String newUsername = (String) ((IInputControl) this.inputControlElements.get(USERNAME)).getParsedVal();
        String password = (String) ((IInputControl) this.inputControlElements.get(CURRENT_PASSWORD)).getParsedVal();
        String newPassword = (String) ((IInputControl) this.inputControlElements.get(NEW_PASSWORD)).getParsedVal();
        String fname = (String) ((IInputControl) this.inputControlElements.get(FNAME)).getParsedVal();
        String lname = (String) ((IInputControl) this.inputControlElements.get(LNAME)).getParsedVal();

        try {
            User updatedUser = Model.getUserDao().update(new User(newUsername, newPassword, fname, lname), username,
                    password);
            this.beforeContainer.getChildren().setAll(new AlertView("User has been successfully created!", "success"));
            // set the updated user
            AppState.getInstance().setUser(updatedUser);
            // rerender main scene to display changes
            AppState.getInstance().switchScene(new Scene(new MainScene()), true);
        } catch (EntityNotFoundException e) {
            this.beforeContainer.getChildren().setAll(new AlertView("User is not found!", "error"));
        } catch (UnauthorisedAction e) {
            this.beforeContainer.getChildren().setAll(new AlertView("Invalid credentials!", "error"));
        } catch (SQLException e) {
            if (e.getErrorCode() == DB.SQLITE_CONSTRAINT) {
                this.beforeContainer.getChildren()
                        .setAll(new AlertView("Username is already taken! Please use another one.", "error"));
            } else {
                this.beforeContainer.getChildren().setAll(new AlertView("Something wrong happend!", "error"));
            }
        }
    }
}
