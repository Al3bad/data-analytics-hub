package dev.alabbad.controllers;

import java.util.LinkedHashMap;

import dev.alabbad.elements.ExtendedPasswordField;
import dev.alabbad.elements.ExtendedTextField;
import dev.alabbad.elements.PrimaryButton;
import dev.alabbad.elements.SecondaryButton;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

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

    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.beforeContainer) == false) {
            return false;
        }
        String username = (String) this.textFieldElements.get(USERNAME).getParsedVal();
        String password = (String) this.textFieldElements.get(PASSWORD).getParsedVal();
        String fname = (String) this.textFieldElements.get(FNAME).getParsedVal();
        String lname = (String) this.textFieldElements.get(LNAME).getParsedVal();

        // Insert user to db
        User newUser = DB.insertUser(username, password, fname, lname);

        if (newUser == null) {
            this.beforeContainer.getChildren().setAll(new CAlert("Something wrong happend!", "error"));
            return false;
        } else {
            this.beforeContainer.getChildren().setAll(new CAlert("User has been successfully created!", "success"));
            AppState.getInstance().setUser(newUser);
            Scene dashboardScene = new Scene(new MainSceneController());
            AppState.getInstance().switchScene(dashboardScene, true);
        }
        return true;
    }

    @Override
    protected void onSecondaryBtnClicked(MouseEvent event) {
        Scene portalScene = new Scene(new CPortalScene(new LoginFormController()));
        AppState.getInstance().switchScene(portalScene, false);
    }
}
