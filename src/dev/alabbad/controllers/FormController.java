package dev.alabbad.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public abstract class FormController extends VBox {
    @FXML
    protected Button primaryBtn;

    @FXML
    protected Button secondryBtn;

    @FXML
    protected VBox statusContainer;

    protected HashMap<String, TextField> textFieldElements = new HashMap<String, TextField>();

    public FormController(String fxmlFilePath) {
        // Load from FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFilePath));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        // get all TextField in the form
        Set<Node> textFields = this.lookupAll(".input");
        // put TextField element in the HashMap
        for (Node textField : textFields) {
            String id = ((TextField) textField).getId();
            this.textFieldElements.put(id, (TextField) textField);
        }

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

    @FXML
    protected abstract Boolean onSubmitBtnClicked(MouseEvent event);

    @FXML
    protected abstract void onCancelBtnClicked(MouseEvent event);
}
