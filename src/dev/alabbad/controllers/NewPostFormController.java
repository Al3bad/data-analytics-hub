package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.Model;
import dev.alabbad.models.Post;
import dev.alabbad.utils.Parser;
import dev.alabbad.views.AlertView;
import dev.alabbad.views.ExtendedTextField;
import dev.alabbad.views.PrimaryButton;
import javafx.scene.input.MouseEvent;

public class NewPostFormController extends FormController {
    // TextField IDs & Labels
    private final static String ID = "ID";
    private final static String AUTHOR = "Author";
    private final static String CONTENT = "Content";
    private final static String LIKES = "Likes";
    private final static String SHARES = "Shares";
    private final static String DATETIME = "Date/Time";

    public NewPostFormController() {
        super(createTextFieldElements(), new PrimaryButton("Post"));
    }

    public static LinkedHashMap<String, ExtendedTextField> createTextFieldElements() {
        LinkedHashMap<String, ExtendedTextField> textFieldElements = new LinkedHashMap<String, ExtendedTextField>();
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
        textFieldElements.put(DATETIME, new ExtendedTextField<String>((val) -> Parser.parseDateTime(val)));
        return textFieldElements;
    }

    @Override
    public Boolean onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.beforeContainer) == false) {
            return false;
        }

        Integer id = (Integer) this.textFieldElements.get(ID).getParsedVal();
        String author = (String) this.textFieldElements.get(AUTHOR).getParsedVal();
        String content = (String) this.textFieldElements.get(CONTENT).getParsedVal();
        int likes = (int) this.textFieldElements.get(LIKES).getParsedVal();
        int shares = (int) this.textFieldElements.get(SHARES).getParsedVal();
        String dateTime = (String) this.textFieldElements.get(DATETIME).getParsedVal();

        // Insert poast to db
        try {
            Post newPost = Model.getPostDao().insert(new Post(id, content, author, likes, shares, dateTime));
            newPost.displayDetails();
            this.beforeContainer.getChildren().setAll(new AlertView("Post has been successfully created!", "success"));
            resetTextFields();
            return true;
        } catch (EntityNotFoundException e) {
            this.beforeContainer.getChildren()
                    .setAll(new AlertView("Post is not found! Something wrong happend!", "error"));
        } catch (SQLException e) {
            this.beforeContainer.getChildren().setAll(new AlertView("Something wrong happend!", "error"));
        }
        return false;
    }
}
