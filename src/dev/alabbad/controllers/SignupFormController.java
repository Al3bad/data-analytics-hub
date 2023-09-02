package dev.alabbad.controllers;

import java.util.HashMap;

import dev.alabbad.exceptions.InvalidFormException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class SignupFormController extends FormController {
    @FXML
    protected TextField username;

    @FXML
    protected PasswordField password;

    @FXML
    protected TextField fname;

    @FXML
    protected TextField lname;

    public SignupFormController() {
        super("/views/signup-form.fxml");
    }

    @FXML
    protected Boolean onSubmitBtnClicked(MouseEvent event) {
        User userDetails = this.validateForm();

        if (userDetails == null) {
            return false;
        }

        // Insert user to db
        User newUser = DB.insertUser(userDetails.getUsername(), userDetails.getPassword(), userDetails.getFirstName(),
                        userDetails.getLastName());

        if (newUser == null) {
            this.statusContainer.getChildren().setAll(new CAlert("Something wrong happend!", "error"));
            return false;
        } else {
            this.statusContainer.getChildren().setAll(new CAlert("User has been successfully created!", "success"));
            AppState.getInstance().setUser(newUser);
            Scene dashboardScene = new Scene(new MainSceneController());
            AppState.getInstance().switchScene(dashboardScene, true);
        }
        return true;
    }

    @FXML
    protected void onCancelBtnClicked(MouseEvent event) {
        Scene portalScene = new Scene(new CPortalScene(new LoginFormController()));
        AppState.getInstance().switchScene(portalScene, false);
    }

    protected User validateForm() {
        String username = this.username.getText();
        String password = this.password.getText();
        String fname = this.fname.getText();
        String lname = this.lname.getText();

        // Validate & parse form
        this.resetTextFieldStyles();
        try {
            return SignupFormController.parseForm(username, fname, lname, password);
        } catch (InvalidFormException e) {
            // change border color of the text input to red
            this.setTextFieldErrorStyles(e.getErrors());
            this.statusContainer.getChildren().setAll(new CAlert("Invalid username or password!", "error"));
            return null;
        }
    }

    private static User parseForm(String username, String fname, String lname, String password)
                    throws InvalidFormException {
        HashMap<String, String> errors = new HashMap<String, String>();
        try {
            username = Parser.parseStr(username, true);
        } catch (Exception e) {
            errors.put("username", "Username cannot be empty");
        }

        try {
            fname = Parser.parseStr(fname);
        } catch (Exception e) {
            errors.put("fname", "First name cannot be empty");
        }

        try {
            lname = Parser.parseStr(lname);
        } catch (Exception e) {
            errors.put("lname", "Last name cannot be empty");
        }

        try {
            password = Parser.parsePassword(password);
        } catch (Exception e) {
            errors.put("password", e.getMessage());
        }

        if (errors.size() > 0) {
            throw new InvalidFormException("Invalid form!", errors);
        }

        return new User(username, fname, lname, password);
    }
}
