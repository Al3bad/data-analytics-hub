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
        this.primaryBtnHandler = () -> this.loginUser();
        this.secondryBtnHandler = secondaryBtnHandler;
        this.setupComponent();
    }

    private void setupComponent() {
        // Bind events to event handlers
        System.out.println("Login");
    }

    private void loginUser() {
        String username = this.username.getText();
        String password = this.password.getText();

        // TODO: validate form
        // ...

        // TODO: hanlde invalid form - show error message in GUI
        // ...

        // TODO: handle valid form
        // ...

        // Insert user to db
        User user = DB.loginUser(username, password);

        if (user == null) {
            this.statusContainer.getChildren().setAll(new CAlert("Incorrect username or password!", "error"));
        } else {
            this.statusContainer.getChildren().setAll(new CAlert("You're successfully logged in", "success"));
            // reset text fields
            this.username.setText("");
            this.password.setText("");
        }
    }
}
