package dev.alabbad.controllers;

import dev.alabbad.exceptions.InvalidFormException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.UserCreds;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LoginFormController extends FormController {
    public LoginFormController() {
        super(createTextFieldElements(), new Button("Login"), new Button("Signup"));
        // NOTE: remove this later
        this.textFieldElements.get("username").setText("xv");
        this.textFieldElements.get("password").setText("1234567");
    }

    public static LinkedHashMap<String, TextField> createTextFieldElements() {
        LinkedHashMap<String, TextField> textFieldElements = new LinkedHashMap<String, TextField>();
        textFieldElements.put("username", new TextField());
        textFieldElements.put("password", new PasswordField());
        return textFieldElements;
    }

    @FXML
    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        UserCreds userCreds;
        String username = this.textFieldElements.get("username").getText();
        String password = this.textFieldElements.get("password").getText();

        // Validate & parse form
        this.resetTextFieldStyles();
        try {
            userCreds = parseForm(username, password);
        } catch (InvalidFormException e) {
            // change border color of the text input to red
            this.setTextFieldErrorStyles(e.getErrors());
            this.beforeContainer.getChildren().setAll(new CAlert("Invalid username or password!", "error"));
            return false;
        }

        // Get user from DB
        User user = DB.loginUser(userCreds.getUsername(), userCreds.getPassword());

        if (user == null) {
            this.beforeContainer.getChildren().setAll(new CAlert("Incorrect username or password!", "error"));
            return false;
        } else {
            // navigate to dashboard scene
            AppState.getInstance().setUser(user);
            Scene dashboardScene = new Scene(new MainSceneController());
            AppState.getInstance().switchScene(dashboardScene, true);
        }
        return true;
    }

    @FXML
    @Override
    protected void onSecondaryBtnClicked(MouseEvent event) {
        Scene portalScene = new Scene(new CPortalScene(new SignupFormController()));
        AppState.getInstance().switchScene(portalScene, false);
    }

    private static UserCreds parseForm(String username, String password) throws InvalidFormException {
        HashMap<String, String> errors = new HashMap<String, String>();
        try {
            username = Parser.parseStr(username);
        } catch (Exception e) {
            errors.put("username", "Username cannot be empty");
        }

        try {
            password = Parser.parsePassword(password);
        } catch (Exception e) {
            errors.put("password", e.getMessage());
        }

        if (errors.size() > 0) {
            throw new InvalidFormException("Invalid form!", errors);
        }

        return new UserCreds(username, password);
    }
}
