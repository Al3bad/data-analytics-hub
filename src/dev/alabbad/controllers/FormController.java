package dev.alabbad.controllers;

import java.util.HashMap;
import java.util.LinkedHashMap;

import dev.alabbad.exceptions.InvalidFormException;
import dev.alabbad.exceptions.ParseValueException;
import dev.alabbad.views.AlertView;
import dev.alabbad.views.ExtendedTextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public abstract class FormController extends VBox {
    protected VBox beforeContainer = new VBox();
    protected VBox btnGroup;
    protected Button primaryBtn;
    protected Button secondaryBtn;
    protected VBox afterContainer = new VBox();

    protected LinkedHashMap<String, ExtendedTextField> textFieldElements = new LinkedHashMap<>();

    public FormController(LinkedHashMap<String, ExtendedTextField> textFields, Button primaryBtn) {
        this.textFieldElements = textFields;
        this.primaryBtn = primaryBtn;

        this.setupForm();
    }

    public FormController(LinkedHashMap<String, ExtendedTextField> textFields, Button primaryBtn, Button secondaryBtn) {
        this.textFieldElements = textFields;
        this.primaryBtn = primaryBtn;
        this.secondaryBtn = secondaryBtn;

        this.setupForm();
        this.secondaryBtn.setId("secondaryBtn");
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

        this.primaryBtn.setId("primaryBtn");
        this.primaryBtn.onMouseClickedProperty().set(event -> this.onPrimaryBtnClicked(event));
        this.btnGroup.getChildren().add(primaryBtn);

        // put everything together
        this.setSpacing(8);
        this.setPrefWidth(200);
        this.getChildren().setAll(inputs, this.beforeContainer, this.btnGroup, this.afterContainer);
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

    protected Boolean validateForm(VBox container) {
        // Validate & parse form
        this.resetTextFieldStyles();

        try {
            return this.parseForm();
        } catch (InvalidFormException e) {
            // change border color of the text input to red
            this.setTextFieldErrorStyles(e.getErrors());
            String formattedErrors = "Invalid form!\n";
            for (String fieldId : e.getErrors().keySet()) {
                String errorMsg = e.getErrors().get(fieldId);
                formattedErrors += String.format("- %s: %s\n", fieldId, errorMsg);
            }
            container.getChildren().setAll(new AlertView(formattedErrors.trim(), "error"));
            return false;
        }
    }

    private Boolean parseForm() throws InvalidFormException {
        HashMap<String, String> errors = new HashMap<String, String>();
        for (String textFieldId : this.textFieldElements.keySet()) {
            try {
                if ((TextField) this.textFieldElements.get(textFieldId) instanceof PasswordField) {
                    System.out.println("IT's a PasswordField");
                }
                this.textFieldElements.get(textFieldId).parse();
            } catch (ParseValueException e) {
                errors.put(textFieldId, e.getMessage());
            }
        }

        if (errors.size() > 0) {
            throw new InvalidFormException("Invalid form!", errors);
        }
        return true;
    }

    protected void onKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            this.onPrimaryBtnClicked(null);
        }
    }

    protected abstract Boolean onPrimaryBtnClicked(MouseEvent event);

    protected void onSecondaryBtnClicked(MouseEvent event) {
        System.out.println("Secondary btn is clicked!");
    }
}
