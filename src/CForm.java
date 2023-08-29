import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.HashMap;

public abstract class CForm extends VBox {
    // reference:
    // https://www.w3docs.com/snippets/java/how-to-pass-a-function-as-a-parameter-in-java.html
    @FunctionalInterface
    interface Function {
        void run();
    }

    protected VBox component;

    @FXML
    protected Button primaryBtn;

    @FXML
    protected Button secondryBtn;

    @FXML
    protected VBox statusContainer;

    protected Function primaryBtnHandler;
    protected Function secondryBtnHandler;
    protected HashMap<String, TextField> textFieldElements = new HashMap<String, TextField>();

    public CForm(String fxmlFilePath) {
        this.loadComponent(fxmlFilePath);
        this.setupComponent();
    }

    private void loadComponent(String fxmlFilePath) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFilePath));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            this.component = fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void setupComponent() {
        // Bind events to event handlers
        this.primaryBtn.onMouseClickedProperty().set(event -> primaryBtnHandler.run());
        this.secondryBtn.onMouseClickedProperty().set(event -> secondryBtnHandler.run());
    }

    protected void resetTextFieldStyles() {
        for (TextField textField : this.textFieldElements.values()) {
            textField.getStyleClass().remove("error");
        }
    }

    protected void setTextFieldErrorStyles(HashMap<String, String> errors) {
        for (String textFieldId : errors.keySet()) {
            if (this.textFieldElements.get(textFieldId) != null) {
                this.textFieldElements.get(textFieldId).getStyleClass().add("error");
            }
        }
    }

    public VBox getComponent() {
        return this.component;
    }
}
