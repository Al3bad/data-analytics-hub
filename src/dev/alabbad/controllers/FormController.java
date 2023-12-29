package dev.alabbad.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;

import dev.alabbad.exceptions.InvalidFormException;
import dev.alabbad.exceptions.ParseValueException;
import dev.alabbad.interfaces.IInputControl;
import dev.alabbad.views.AlertView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;

/**
 * The abstract base class for form controller. It construct the the form
 * dynamically. And it has methods to validate input fields and display error
 * messages if any input is not valid
 *
 * @author Abdullah Alabbad
 * @version 2.0.0
 */
public abstract class FormController extends VBox {
    protected VBox beforeContainer = new VBox();
    protected VBox btnGroup;
    protected Button primaryBtn;
    protected Button secondaryBtn;
    protected VBox afterContainer = new VBox();

    // This linked hash map holds all input control for the contructed form. The
    // reason
    // why linked hash map is used to to respect the order element when they're
    // added
    protected LinkedHashMap<String, Control> inputControlElements = new LinkedHashMap<>();

    public FormController(LinkedHashMap<String, Control> inputControls, Button primaryBtn) {
        this.inputControlElements = inputControls;
        this.primaryBtn = primaryBtn;

        this.setupForm();
    }

    public FormController(LinkedHashMap<String, Control> inputControls, Button primaryBtn, Button secondaryBtn) {
        this.inputControlElements = inputControls;
        this.primaryBtn = primaryBtn;
        this.secondaryBtn = secondaryBtn;

        this.setupForm();
        this.secondaryBtn.setId("secondaryBtn");
        this.secondaryBtn.onMouseClickedProperty().set(event -> this.onSecondaryBtnClicked(event));
        this.btnGroup.getChildren().add(this.secondaryBtn);
    }

    /**
     * Dynamically construct the form
     */
    protected void setupForm() {
        this.getChildren().removeAll();
        // construct input fields
        VBox inputs = new VBox();
        inputs.setSpacing(8);
        inputs.setId("inputGroup");

        for (String id : this.inputControlElements.keySet()) {
            inputs.getChildren().add(createInput(id, this.inputControlElements.get(id)));
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

    protected VBox createInput(String label, Control inputNode) {
        VBox inputContainer = new VBox();
        Label _label = new Label(label);
        inputNode.setId(label);
        inputNode.getStylesheets().add("/css/input.css");
        inputNode.getStyleClass().add("input");
        inputNode.onKeyPressedProperty().set(event -> this.onKeyPressed(event));
        inputContainer.getChildren().addAll(_label, inputNode);
        return inputContainer;
    }

    /**
     * Reset styles for all input controls in the form
     */
    protected void resetInputControlsStyles() {
        for (Control inputControl : this.inputControlElements.values()) {
            inputControl.getStyleClass().remove("error");
        }
    }

    /**
     * Add error styles for the input controls with invalid values
     *
     * @param errors
     */
    protected void setInputControlsErrorStyles(HashMap<String, String> errors) {
        for (String inputControlId : errors.keySet()) {
            if (this.inputControlElements.get(inputControlId) != null) {
                this.inputControlElements.get(inputControlId).getStyleClass().add("error");
            }
        }
    }

    /**
     * Reset styles content of input controls in the form
     */
    protected void resetInputControls() {
        this.resetInputControlsStyles();
        for (Control inputControl : this.inputControlElements.values()) {
            if (inputControl instanceof TextField) {
                ((TextField) inputControl).setText("");
            } else if (inputControl instanceof DatePicker) {
                ((DatePicker) inputControl).setValue(LocalDate.now());
            }
        }
    }

    /**
     * Validate the values typed in each input control in the form
     *
     * @param container for the alert element to display the error messages if any
     * @return true if the form is valid, false, otherwise
     */
    protected Boolean validateForm(VBox container) {
        // Validate & parse form
        this.resetInputControlsStyles();

        try {
            return this.parseForm();
        } catch (InvalidFormException e) {
            // change border color of the text input to red
            this.setInputControlsErrorStyles(e.getErrors());
            String formattedErrors = "Invalid form!\n";
            for (String fieldId : e.getErrors().keySet()) {
                String errorMsg = e.getErrors().get(fieldId);
                formattedErrors += String.format("- %s: %s\n", fieldId, errorMsg);
            }
            container.getChildren().setAll(new AlertView(formattedErrors.trim(), "error"));
            return false;
        }
    }

    /**
     * Parse the value in each input control in the form by called in the parser
     *
     * @return true if no errors in the input controls, false, otherwise
     * @throws InvalidFormException when the form is invalid
     */
    private Boolean parseForm() throws InvalidFormException {
        HashMap<String, String> errors = new HashMap<String, String>();
        for (String inputControlId : this.inputControlElements.keySet()) {
            try {
                if (this.inputControlElements.get(inputControlId) instanceof IInputControl) {
                    ((IInputControl) this.inputControlElements.get(inputControlId)).parse();
                } else {
                    System.out.println(
                            "[Warning] Input controll cannot be parsed because it does not conform to IInputControl interface!");
                }
            } catch (ParseValueException e) {
                errors.put(inputControlId, e.getMessage());
            }
        }

        if (errors.size() > 0) {
            throw new InvalidFormException("Invalid form!", errors);
        }
        return true;
    }

    /**
     * Call the primary button handler when the enter key is pressed
     *
     * @param event key event
     */
    protected void onKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            this.onPrimaryBtnClicked(null);
        }
    }

    /**
     * The primary button handler that needs to be implemented by the sub-classes
     *
     * @param event mouse event
     */
    protected abstract void onPrimaryBtnClicked(MouseEvent event);

    /**
     * The optional secondary button handler
     *
     * @param event mouse event
     */
    protected void onSecondaryBtnClicked(MouseEvent event) {
        System.out.println("Secondary btn is clicked!");
    }
}
