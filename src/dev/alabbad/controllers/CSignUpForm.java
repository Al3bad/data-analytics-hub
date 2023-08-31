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

public class CSignUpForm extends CForm {
    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private TextField fname;

    @FXML
    private TextField lname;

    private Function secondryBtnHandler;

    public CSignUpForm(Function secondaryBtnHandler) {
        super("/views/signup-form.fxml");
        this.secondryBtnHandler = secondaryBtnHandler;
        this.textFieldElements.put("username", username);
        this.textFieldElements.put("password", password);
        this.textFieldElements.put("fname", fname);
        this.textFieldElements.put("lname", lname);
        this.setupComponent();
    }

    private void setupComponent() {
        // Bind events to event handlers
        System.out.println("Signup");
    }

    protected Boolean onSubmit() {
        User userDetails;
        String username = this.username.getText();
        String password = this.password.getText();
        String fname = this.fname.getText();
        String lname = this.lname.getText();

        // Validate & parse form
        this.resetTextFieldStyles();
        try {
            userDetails = CSignUpForm.parseForm(username, fname, lname, password);
        } catch (InvalidFormException e) {
            // change border color of the text input to red
            this.setTextFieldErrorStyles(e.getErrors());
            this.statusContainer.getChildren().setAll(new CAlert("Invalid username or password!", "error"));
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
            Scene dashboardScene = new Scene(new DashboardSceneController().getComponent());
            AppState.getInstance().switchScene(dashboardScene, 800, 400, true);
        }
        return true;
    }

    protected void onCancel() {
        this.secondryBtnHandler.run();
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
