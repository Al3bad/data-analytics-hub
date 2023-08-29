import java.util.HashMap;

import javafx.fxml.FXML;
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

    public CSignUpForm(Function secondaryBtnHandler) {
        super("fxml/signup-form.fxml");
        this.primaryBtnHandler = () -> this.createUser();
        this.secondryBtnHandler = secondaryBtnHandler;
        this.setupComponent();
    }

    private void setupComponent() {
        // Bind events to event handlers
        System.out.println("Signup");
    }

    private Boolean createUser() {
        User userDetails;
        String username = this.username.getText();
        String password = this.password.getText();
        String fname = this.fname.getText();
        String lname = this.lname.getText();

        // Validate & parse form
        this.username.getStyleClass().remove("error");
        this.fname.getStyleClass().remove("error");
        this.lname.getStyleClass().remove("error");
        this.password.getStyleClass().remove("error");
        try {
            userDetails = CSignUpForm.parseForm(username, fname, lname, password);
        } catch (InvalidFormException e) {
            // change border color of the text input to red
            if (e.getErrors().get("username") != null) {
                this.username.getStyleClass().add("error");
            }
            if (e.getErrors().get("fname") != null) {
                this.fname.getStyleClass().add("error");
            }
            if (e.getErrors().get("lname") != null) {
                this.lname.getStyleClass().add("error");
            }
            if (e.getErrors().get("password") != null) {
                this.password.getStyleClass().add("error");
            }
            this.statusContainer.getChildren().setAll(new CAlert("Invalid username or password!", "error"));
            return false;
        }

        // Insert user to db
        User newUser = DB.insertUser(username, password, fname, lname);

        if (newUser == null) {
            this.statusContainer.getChildren().setAll(new CAlert("Something wrong happend!", "error"));
            return false;
        } else {
            this.statusContainer.getChildren().setAll(new CAlert("User has been successfully created!", "success"));
            // TODO: navigate to dashboard scene
            // reset text fields
            this.username.setText("");
            this.password.setText("");
            this.fname.setText("");
            this.lname.setText("");
        }
        return true;
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
