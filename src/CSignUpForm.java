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

    private void createUser() {
        String username = this.username.getText();
        String password = this.password.getText();
        String fname = this.fname.getText();
        String lname = this.lname.getText();

        // TODO: validate form
        // ...

        // TODO: hanlde invalid form - show error message in GUI
        // ...

        // TODO: handle valid form
        // ...

        // Insert user to db
        User newUser = DB.insertUser(username, password, fname, lname);

        // TODO: show msg in GUI
        if (newUser == null) {
            this.statusContainer.getChildren().setAll(new CAlert("Something wrong happend!", "error"));
        } else {
            this.statusContainer.getChildren().setAll(new CAlert("User has been successfully created!", "success"));
            // reset text fields
            this.username.setText("");
            this.password.setText("");
            this.fname.setText("");
            this.lname.setText("");
        }
    }
}
