package dev.alabbad.controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class FormController extends VBox {
    protected VBox beforeContainer = new VBox();
    protected VBox btnGroup;
    protected Button primaryBtn;
    protected Button secondaryBtn;
    protected VBox afterContainer = new VBox();

    protected LinkedHashMap<String, TextField> textFieldElements = new LinkedHashMap<String, TextField>();

    public FormController(LinkedHashMap<String, TextField> textFields, Button primaryBtn) {
        this.textFieldElements = textFields;
        this.primaryBtn = primaryBtn;

        this.setupForm();
    }

    public FormController(LinkedHashMap<String, TextField> textFields, Button primaryBtn, Button secondaryBtn) {
        this.textFieldElements = textFields;
        this.primaryBtn = primaryBtn;
        this.secondaryBtn = secondaryBtn;

        this.setupForm();
        this.setupButton(this.secondaryBtn, "secondaryBtn", "secondary");
        this.secondaryBtn.onMouseClickedProperty().set(event -> this.onSecondaryBtnClicked(event));
        this.btnGroup.getChildren().add(this.secondaryBtn);
    }

    protected void setupForm() {
        this.getChildren().removeAll();
        // construct input fields
        VBox inputs = new VBox();
        inputs.setSpacing(8);
        inputs.setId("inputGroup");

        for (String id : this.textFieldElements.keySet()) {
            System.out.println(id);
            VBox inputContainer = new VBox();
            Label label = new Label(id);
            TextField textField = this.textFieldElements.get(id);
            textField.setId(id);
            textField.getStylesheets().add("/css/input.css");
            textField.getStyleClass().add("input");
            textField.onKeyPressedProperty().set(event -> this.onKeyPressed(event));
            inputContainer.getChildren().addAll(label, textField);
            inputs.getChildren().add(inputContainer);
        }

        // construct buttons
        this.btnGroup = new VBox();
        this.btnGroup.setId("btnGroup");
        this.btnGroup.setSpacing(8);

        this.setupButton(this.primaryBtn, "primaryBtn", "primary");
        this.primaryBtn.onMouseClickedProperty().set(event -> this.onPrimaryBtnClicked(event));
        this.btnGroup.getChildren().add(primaryBtn);

        // put everything together
        this.setSpacing(8);
        this.setPrefWidth(200);
        this.getChildren().setAll(inputs, this.beforeContainer, this.btnGroup, this.afterContainer);
    }

    protected void setupButton(Button btn, String id, String type) {
        btn.setId(id);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStylesheets().add("/css/button.css");
        btn.getStyleClass().add("button");
        btn.getStyleClass().add(type);
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

    protected void resetTextFields() {
        this.resetTextFieldStyles();
        for (TextField textField : this.textFieldElements.values()) {
            textField.setText("");
        }
    }

    protected void onKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            this.onPrimaryBtnClicked(null);
        }
    }

    protected abstract Boolean onPrimaryBtnClicked(MouseEvent event);

    protected abstract void onSecondaryBtnClicked(MouseEvent event);
}
