package dev.alabbad.controllers;

import java.sql.SQLException;

import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.exceptions.UserNotFoundException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;
import dev.alabbad.views.AlertView;
import dev.alabbad.views.ExtendedPasswordField;
import dev.alabbad.views.MainScene;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public class EditProfileFormController extends SignupFormController {
    // TextField IDs & Labels
    protected final static String CURRENT_PASSWORD = "Current Password";
    protected final static String NEW_PASSWORD = "New Password";

    public EditProfileFormController() {
        super();
        this.setAlignment(Pos.CENTER);
        this.textFieldElements.remove(PASSWORD);
        this.textFieldElements.put(CURRENT_PASSWORD,
                        new ExtendedPasswordField<String>((val) -> Parser.parseStr(val, false)));
        this.textFieldElements.put(NEW_PASSWORD,
                        new ExtendedPasswordField<String>((val) -> Parser.parseStr(val, false)));
        this.setupForm();
        this.fillinForm();
    }

    private void fillinForm() {
        User loggedinUser = AppState.getInstance().getUser();
        this.textFieldElements.get(USERNAME).setText(loggedinUser.getUsername());
        this.textFieldElements.get(FNAME).setText(loggedinUser.getFirstName());
        this.textFieldElements.get(LNAME).setText(loggedinUser.getLastName());
        this.primaryBtn.setText("Edit");
    }

    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.beforeContainer) == false) {
            return false;
        }
        String newUsername = (String) this.textFieldElements.get(USERNAME).getParsedVal();
        String password = (String) this.textFieldElements.get(CURRENT_PASSWORD).getParsedVal();
        String newPassword = (String) this.textFieldElements.get(NEW_PASSWORD).getParsedVal();
        String fname = (String) this.textFieldElements.get(FNAME).getParsedVal();
        String lname = (String) this.textFieldElements.get(LNAME).getParsedVal();

        try {
            String username = AppState.getInstance().getUser().getUsername();
            User updatedUser = DB.updateUser(username, newUsername, password, newPassword, fname, lname);
            this.beforeContainer.getChildren().setAll(new AlertView("User has been successfully created!", "success"));
            AppState.getInstance().setUser(updatedUser);
            Scene dashboardScene = new Scene(new MainScene());
            AppState.getInstance().switchScene(dashboardScene, true);
        } catch (UserNotFoundException e) {
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
