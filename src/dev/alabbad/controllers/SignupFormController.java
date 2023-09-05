package dev.alabbad.controllers;

import java.util.HashMap;
import java.util.LinkedHashMap;

import dev.alabbad.exceptions.InvalidFormException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class SignupFormController extends FormController {
    public SignupFormController() {
        super(createTextFieldElements(), new Button("Signup"), new Button("Login"));
    }

    public static LinkedHashMap<String, TextField> createTextFieldElements() {
        LinkedHashMap<String, TextField> textFieldElements = new LinkedHashMap<String, TextField>();
        textFieldElements.put("Username", new TextField());
        textFieldElements.put("First Name", new TextField());
        textFieldElements.put("Last Name", new TextField());
        textFieldElements.put("Password", new PasswordField());
        return textFieldElements;
    }

    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        User userDetails = this.validateForm();

        if (userDetails == null) {
            return false;
        }

        // Insert user to db
        User newUser = DB.insertUser(userDetails.getUsername(), userDetails.getPassword(), userDetails.getFirstName(),
                        userDetails.getLastName());

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

    protected User validateForm() {
        String username = this.textFieldElements.get("Username").getText();
        String fname = this.textFieldElements.get("First Name").getText();
        String lname = this.textFieldElements.get("Last Name").getText();
        String password = this.textFieldElements.get("Password").getText();

        // Validate & parse form
        this.resetTextFieldStyles();
        try {
            return parseForm(username, fname, lname, password);
        } catch (InvalidFormException e) {
            // change border color of the text input to red
            this.setTextFieldErrorStyles(e.getErrors());
            this.beforeContainer.getChildren().setAll(new CAlert("Invalid username or password!", "error"));
            return null;
        }
    }

    private static User parseForm(String username, String fname, String lname, String password)
                    throws InvalidFormException {
        HashMap<String, String> errors = new HashMap<String, String>();
        try {
            username = Parser.parseStr(username, true);
        } catch (Exception e) {
            errors.put("Username", "Username cannot be empty");
        }

        try {
            fname = Parser.parseStr(fname);
        } catch (Exception e) {
            errors.put("First Name", "First name cannot be empty");
        }

        try {
            lname = Parser.parseStr(lname);
        } catch (Exception e) {
            errors.put("Last Name", "Last name cannot be empty");
        }

        try {
            password = Parser.parsePassword(password);
        } catch (Exception e) {
            errors.put("Password", e.getMessage());
        }

        if (errors.size() > 0) {
            throw new InvalidFormException("Invalid form!", errors);
        }

        return new User(username, fname, lname, password);
    }
}
