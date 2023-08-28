import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CLoginUserForm extends VBox {
    private VBox component;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private VBox statusContainer;

    public CLoginUserForm() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/login-user-form.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            this.component = fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        setupElements();
    }

    private void setupElements() {
        // Bind events to event handlers
        loginBtn.onMouseClickedProperty().set(event -> createUser());
    }

    private void createUser() {
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

    public VBox getComponent() {
        return this.component;
    }
}
