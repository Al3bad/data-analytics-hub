import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import java.io.IOException;

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

    public CForm(String fxmlFilePath) {
        loadComponent(fxmlFilePath);
        setupComponent();
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

    public VBox getComponent() {
        return this.component;
    }
}
