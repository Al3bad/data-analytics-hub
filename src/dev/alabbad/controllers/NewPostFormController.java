package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

import dev.alabbad.interfaces.IInputControl;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.Model;
import dev.alabbad.models.Post;
import dev.alabbad.utils.Parser;
import dev.alabbad.views.AlertView;
import dev.alabbad.views.ExtendedTextField;
import dev.alabbad.views.PrimaryButton;
import dev.alabbad.views.DateTimePicker;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;

/**
 * Implementation of new post
 *
 * @author Abdullah Alabbad
 * @version 1.0.1
 */
public class NewPostFormController extends FormController {
    // TextField IDs & Labels
    private final static String ID = "ID";
    private final static String AUTHOR = "Author";
    private final static String CONTENT = "Content";
    private final static String LIKES = "Likes";
    private final static String SHARES = "Shares";
    private final static String DATETIME = "Date/Time";

    public NewPostFormController() {
        super(createInputElements(), new PrimaryButton("Post"));
    }

    /**
     * Add the text fields for this form
     *
     * @return linked hash map containing the text field elements
     */
    public static LinkedHashMap<String, Control> createInputElements() {
        LinkedHashMap<String, Control> textFieldElements = new LinkedHashMap<String, Control>();
        textFieldElements.put(ID, new ExtendedTextField<Integer>((val) -> Parser.parseInt(val, 0, true)));
        ExtendedTextField<String> authorField = new ExtendedTextField<String>((val) -> Parser.parseStr(val, false));
        // Author field -----------------------------
        authorField.setText(AppState.getInstance().getUser().getUsername());
        authorField.setDisable(true);
        textFieldElements.put(AUTHOR, authorField);
        // -------------------------------------------
        textFieldElements.put(CONTENT, new ExtendedTextField<String>((val) -> Parser.parseStr(val, true)));
        textFieldElements.put(LIKES, new ExtendedTextField<Integer>((val) -> Parser.parseInt(val, 0)));
        textFieldElements.put(SHARES, new ExtendedTextField<Integer>((val) -> Parser.parseInt(val, 0)));
        textFieldElements.put(DATETIME, new DateTimePicker((val) -> Parser.parseDateTime(val)));
        return textFieldElements;
    }

    /**
     * Primary button handler - create new post
     *
     * @param event mouse event
     */
    @Override
    public void onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.beforeContainer) == false) {
            return;
        }

        // get values from input fields
        Integer id = (Integer) ((IInputControl) this.inputControlElements.get(ID)).getParsedVal();
        String author = (String) ((IInputControl) this.inputControlElements.get(AUTHOR)).getParsedVal();
        String content = (String) ((IInputControl) this.inputControlElements.get(CONTENT)).getParsedVal();
        int likes = (int) ((IInputControl) this.inputControlElements.get(LIKES)).getParsedVal();
        int shares = (int) ((IInputControl) this.inputControlElements.get(SHARES)).getParsedVal();
        String dateTime = (String) ((IInputControl) this.inputControlElements.get(DATETIME)).getParsedVal();

        // Insert post to db
        try {
            Model.getPostDao().insert(new Post(id, content, author, likes, shares, dateTime));
            this.beforeContainer.getChildren().setAll(new AlertView("Post has been successfully created!", "success"));
            resetInputControls();
            ((ExtendedTextField) this.inputControlElements.get(AUTHOR))
                    .setText(AppState.getInstance().getUser().getUsername());
        } catch (SQLException e) {
            if (e.getErrorCode() == DB.SQLITE_CONSTRAINT) {
                this.beforeContainer.getChildren()
                        .setAll(new AlertView("This ID is already taken! Please use another one.", "error"));
            } else {
                this.beforeContainer.getChildren().setAll(new AlertView("Something wrong happend!", "error"));
            }
        }
    }
}
