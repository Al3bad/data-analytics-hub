package dev.alabbad.controllers;

import dev.alabbad.exceptions.InvalidFormException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.UserCreds;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;

import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CLoginForm extends CForm {
    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    private Function secondaryBtnHandler;

    public CLoginForm(Function secondaryBtnHandler) {
        super("/views/login-form.fxml");
        // setting up some properties in the parent class
        this.secondaryBtnHandler = secondaryBtnHandler;
        this.textFieldElements.put("username", username);
        this.textFieldElements.put("password", password);
    }

    @Override
    protected Boolean onSubmit() {
        UserCreds userCreds;
        String username = this.username.getText();
        String password = this.password.getText();

        // Validate & parse form
        this.resetTextFieldStyles();
        try {
            userCreds = CLoginForm.parseForm(username, password);
        } catch (InvalidFormException e) {
            // change border color of the text input to red
            this.setTextFieldErrorStyles(e.getErrors());
            this.statusContainer.getChildren().setAll(new CAlert("Invalid username or password!", "error"));
            return false;
        }

        // Get user from DB
        User user = DB.loginUser(userCreds.getUsername(), userCreds.getPassword());

        if (user == null) {
            this.statusContainer.getChildren().setAll(new CAlert("Incorrect username or password!", "error"));
            return false;
        } else {
            // navigate to dashboard scene
            AppState.getInstance().setUser(user);
            Scene dashboardScene = new Scene(new DashboardSceneController().getComponent());
            AppState.getInstance().switchScene(dashboardScene, 800, 400, true);
        }
        return true;
    }

    @Override
    protected void onCancel() {
        this.secondaryBtnHandler.run();
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
