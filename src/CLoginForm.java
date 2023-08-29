import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CLoginForm extends CForm {
    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    public CLoginForm(Function secondaryBtnHandler) {
        super("fxml/login-form.fxml");
        // setting up some properties in the parent class
        this.primaryBtnHandler = () -> this.loginUser();
        this.secondryBtnHandler = secondaryBtnHandler;
        this.textFieldElements.put("username", username);
        this.textFieldElements.put("password", password);
    }

    private Boolean loginUser() {
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
            this.statusContainer.getChildren().setAll(new CAlert("You're successfully logged in", "success"));
            // TODO: navigate to dashboard scene
            // reset text fields
            this.username.setText("");
            this.password.setText("");
        }
        return true;
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
