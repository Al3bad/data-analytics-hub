import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CCreateUserForm extends VBox {
    private Parent component;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private TextField fname;

    @FXML
    private TextField lname;

    @FXML
    private Button createBtn;

    @FXML
    private Button cancelBtn;

    public CCreateUserForm() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/create-user-form.fxml"));
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
        createBtn.onMouseClickedProperty().set(event -> createUser());
    }

    private void createUser() {
        // TODO: validate form
        System.out.println(username.getText() + fname.getText() + lname.getText() + password.getText());

        // TODO: handle valid form
        // ...

        // TODO: hanlde invalid form
        // ...
    }

    public Parent getComponent() {
        return this.component;
    }
}
