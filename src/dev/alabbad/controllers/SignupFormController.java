package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.interfaces.IInputControl;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.Model;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;
import dev.alabbad.views.AlertView;
import dev.alabbad.views.ExtendedPasswordField;
import dev.alabbad.views.ExtendedTextField;
import dev.alabbad.views.MainScene;
import dev.alabbad.views.PortalScene;
import dev.alabbad.views.PrimaryButton;
import dev.alabbad.views.SecondaryButton;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;

/**
 * Implementation of the signup form
 *
 * @author Abdullah Alabbad
 * @version 1.0.1
 */
public class SignupFormController extends FormController {
    // TextField IDs & Labels
    protected final static String USERNAME = "Username";
    protected final static String PASSWORD = "Password";
    protected final static String FNAME = "First Name";
    protected final static String LNAME = "Last Name";

    public SignupFormController() {
        super(createInputElements(), new PrimaryButton("Signup"), new SecondaryButton("Login"));
    }

    public static LinkedHashMap<String, Control> createInputElements() {
        LinkedHashMap<String, Control> textFieldElements = new LinkedHashMap<String, Control>();
        textFieldElements.put(USERNAME, new ExtendedTextField<String>((val) -> Parser.parseStr(val, false)));
        textFieldElements.put(FNAME, new ExtendedTextField<String>((val) -> Parser.parseStr(val, false)));
        textFieldElements.put(LNAME, new ExtendedTextField<String>((val) -> Parser.parseStr(val, false)));
        textFieldElements.put(PASSWORD, new ExtendedPasswordField<String>((val) -> Parser.parsePassword(val)));
        return textFieldElements;
    }

    /**
     * Primary button handler - signup user
     *
     * @param event mouse event
     */
    @Override
    protected void onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.beforeContainer) == false) {
            return;
        }

        // get values from input fields
        String username = (String) ((IInputControl) this.inputControlElements.get(USERNAME)).getParsedVal();
        String password = (String) ((IInputControl) this.inputControlElements.get(PASSWORD)).getParsedVal();
        String fname = (String) ((IInputControl) this.inputControlElements.get(FNAME)).getParsedVal();
        String lname = (String) ((IInputControl) this.inputControlElements.get(LNAME)).getParsedVal();

        // Insert user to db
        try {
            User newUser = Model.getUserDao().insert(new User(username, password, fname, lname));
            // set logged in user
            AppState.getInstance().setUser(newUser);
            // navigate to dashboard scene
            AppState.getInstance().switchScene(new Scene(new MainScene()), true);
        } catch (EntityNotFoundException e) {
            this.beforeContainer.getChildren().setAll(
                    new AlertView("Couldn't get user after signup! Please contact the developer!", "error"));
        } catch (SQLException e) {
            if (e.getErrorCode() == DB.SQLITE_CONSTRAINT) {
                this.beforeContainer.getChildren()
                        .setAll(new AlertView("Username is already taken! Please use another one.", "error"));
            } else {
                this.beforeContainer.getChildren().setAll(new AlertView("Something wrong happend!", "error"));
            }
        }
    }

    /**
     * Secondary button handler - switch to login form
     *
     * @param event mouse event
     */
    @Override
    protected void onSecondaryBtnClicked(MouseEvent event) {
        Scene portalScene = new Scene(new PortalScene(new LoginFormController()));
        AppState.getInstance().switchScene(portalScene, false);
    }
}
