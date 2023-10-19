package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

import dev.alabbad.exceptions.UnauthorisedAction;
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
 * Implementation of the login form
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class LoginFormController extends FormController {
    // TextField IDs & Labels
    private final static String USERNAME = "Username";
    private final static String PASSWORD = "Password";

    public LoginFormController() {
        super(createTextFieldElements(), new PrimaryButton("Login"), new SecondaryButton("Signup"));
    }

    /**
     * Add the text fields for this form
     *
     * @return linked hash map containing the text field elements
     */
    public static LinkedHashMap<String, ExtendedTextField> createTextFieldElements() {
        LinkedHashMap<String, ExtendedTextField> textFieldElements = new LinkedHashMap<String, ExtendedTextField>();
        textFieldElements.put(USERNAME, new ExtendedTextField<String>((val) -> Parser.parseStr(val, false)));
        textFieldElements.put(PASSWORD, new ExtendedPasswordField<String>((val) -> Parser.parseStr(val, true)));
        return textFieldElements;
    }

    /**
     * Primary button handler - login user
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

        // Get user from DB
        try {
            User user = Model.getUserDao().login(username, password);
            // set logged in user
            AppState.getInstance().setUser(user);
            // navigate to dashboard scene
            AppState.getInstance().switchScene(new Scene(new MainScene()), true);
            return true;
        } catch (EntityNotFoundException e) {
            this.beforeContainer.getChildren().setAll(new AlertView("Incorrect username or password!", "error"));
        } catch (UnauthorisedAction e) {
            this.beforeContainer.getChildren().setAll(new AlertView("Incorrect username or password!", "error"));
        } catch (SQLException e) {
            this.beforeContainer.getChildren()
                            .setAll(new AlertView("Something wrong happend! Please contact the developer!", "error"));
        }
        return false;
    }

    /**
     * Secondary button handler - switch to signup form
     *
     * @param event mouse event
     */
    @Override
    protected void onSecondaryBtnClicked(MouseEvent event) {
        AppState.getInstance().switchScene(new Scene(new PortalScene(new SignupFormController())), false);
    }
}
