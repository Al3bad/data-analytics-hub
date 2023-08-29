import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class CPortalScene extends AnchorPane {
    private AnchorPane component;

    @FXML
    private VBox container;

    public CPortalScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/portal-scene.fxml"));
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
        container.getChildren().add(new CLoginForm(() -> this.changeForm(false)).getComponent());
    }

    private void changeForm(Boolean displayRegForm) {
        if (displayRegForm == true) {
            container.getChildren().setAll(new CLoginForm(() -> this.changeForm(false)).getComponent());
        } else {
            container.getChildren().setAll(new CSignUpForm(() -> this.changeForm(true)).getComponent());
        }
    }

    public AnchorPane getComponent() {
        return this.component;
    }

    public VBox getContainer() {
        return this.container;
    }
}
