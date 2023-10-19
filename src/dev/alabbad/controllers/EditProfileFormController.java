package dev.alabbad.controllers;

import java.sql.SQLException;

import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.Model;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;
import dev.alabbad.views.AlertView;
import dev.alabbad.views.ExtendedPasswordField;
import dev.alabbad.views.MainScene;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

/**
 * Implementation of edit profile form
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class EditProfileFormController extends SignupFormController {
    // TextField IDs & Labels
    protected final static String CURRENT_PASSWORD = "Current Password";
    protected final static String NEW_PASSWORD = "New Password";

    public EditProfileFormController() {
        super();
        this.setAlignment(Pos.CENTER);
        this.textFieldElements.remove(PASSWORD);
        this.textFieldElements.put(CURRENT_PASSWORD,
                        new ExtendedPasswordField<String>((val) -> Parser.parseStr(val, true)));
        this.textFieldElements.put(NEW_PASSWORD,
                        new ExtendedPasswordField<String>((val) -> Parser.parseStr(val, true, true)));
        this.setupForm();
        this.fillinForm();
    }

    /**
     * Fill in the form with the details of the loagged in user
     */
    private void fillinForm() {
        User loggedinUser = AppState.getInstance().getUser();
        this.textFieldElements.get(USERNAME).setText(loggedinUser.getUsername());
        this.textFieldElements.get(FNAME).setText(loggedinUser.getFirstName());
        this.textFieldElements.get(LNAME).setText(loggedinUser.getLastName());
        this.primaryBtn.setText("Edit");
    }

    /**
     * Primary button handler - edit user's profile
     *
     * @param event mouse evnet
     */
    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.beforeContainer) == false) {
            return false;
        }

        // get values from text fields
        String username = AppState.getInstance().getUser().getUsername();
        String newUsername = (String) this.textFieldElements.get(USERNAME).getParsedVal();
        String password = (String) this.textFieldElements.get(CURRENT_PASSWORD).getParsedVal();
        String newPassword = (String) this.textFieldElements.get(NEW_PASSWORD).getParsedVal();
        String fname = (String) this.textFieldElements.get(FNAME).getParsedVal();
        String lname = (String) this.textFieldElements.get(LNAME).getParsedVal();

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
            return false;
        } catch (UnauthorisedAction e) {
            this.beforeContainer.getChildren().setAll(new AlertView("Invalid credentials!", "error"));
        } catch (SQLException e) {
            if (e.getErrorCode() == DB.SQLITE_CONSTRAINT) {
                this.beforeContainer.getChildren()
                                .setAll(new AlertView("Username is already taken! Please use another one.", "error"));
            } else {
                this.beforeContainer.getChildren().setAll(new AlertView("Something wrong happend!!!", "error"));
            }
        }
        return true;
    }
}
