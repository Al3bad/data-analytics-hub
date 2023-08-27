import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CCreateUserForm extends VBox {
    private VBox component;

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

    @FXML
    private VBox statusContainer;

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

    public VBox getComponent() {
        return this.component;
    }
}
