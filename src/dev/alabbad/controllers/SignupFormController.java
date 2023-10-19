package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.models.AppState;
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
import javafx.scene.input.MouseEvent;

/**
 * Implementation of the signup form
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class SignupFormController extends FormController {
    // TextField IDs & Labels
    protected final static String USERNAME = "Username";
    protected final static String PASSWORD = "Password";
    protected final static String FNAME = "First Name";
    protected final static String LNAME = "Last Name";

    public SignupFormController() {
        super(createTextFieldElements(), new PrimaryButton("Signup"), new SecondaryButton("Login"));
    }

    public static LinkedHashMap<String, ExtendedTextField> createTextFieldElements() {
        LinkedHashMap<String, ExtendedTextField> textFieldElements = new LinkedHashMap<String, ExtendedTextField>();
        textFieldElements.put(USERNAME, new ExtendedTextField<String>((val) -> Parser.parseStr(val, false)));
        textFieldElements.put(FNAME, new ExtendedTextField<String>((val) -> Parser.parseStr(val, false)));
        textFieldElements.put(LNAME, new ExtendedTextField<String>((val) -> Parser.parseStr(val, false)));
        textFieldElements.put(PASSWORD, new ExtendedPasswordField<String>((val) -> Parser.parseStr(val, false)));
        return textFieldElements;
    }

    /**
     * Primary button handler - signup user
     *
     * @param event mouse event
     */
    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.beforeContainer) == false) {
            return false;
        }

        // get values from input fields
        String username = (String) this.textFieldElements.get(USERNAME).getParsedVal();
        String password = (String) this.textFieldElements.get(PASSWORD).getParsedVal();
        String fname = (String) this.textFieldElements.get(FNAME).getParsedVal();
        String lname = (String) this.textFieldElements.get(LNAME).getParsedVal();

        // Insert user to db
        try {
            User newUser = Model.getUserDao().insert(new User(username, password, fname, lname));
            // set logged in user
            AppState.getInstance().setUser(newUser);
            // navigate to dashboard scene
            AppState.getInstance().switchScene(new Scene(new MainScene()), true);
            return true;
        } catch (EntityNotFoundException e) {
            this.beforeContainer.getChildren().setAll(
                            new AlertView("Couldn't get user after signup! Please contact the developer!", "error"));
        } catch (SQLException e) {
            this.beforeContainer.getChildren().setAll(new AlertView("Something wrong happend!", "error"));
        }
        return false;
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
