package dev.alabbad.controllers;

import java.util.HashMap;
import java.util.LinkedHashMap;

import dev.alabbad.exceptions.InvalidFormException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.Post;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class NewPostFormController extends FormController {
    public NewPostFormController() {
        super(createTextFieldElements(), new Button("Post"));
    }

    public static LinkedHashMap<String, TextField> createTextFieldElements() {
        LinkedHashMap<String, TextField> textFieldElements = new LinkedHashMap<String, TextField>();
        textFieldElements.put("ID", new TextField());
        textFieldElements.put("Content", new TextField());
        textFieldElements.put("Likes", new TextField());
        textFieldElements.put("Shares", new TextField());
        textFieldElements.put("Date/Time", new TextField());
        return textFieldElements;
    }

    @FXML
    public Boolean onPrimaryBtnClicked(MouseEvent event) {
        Post postDetails = this.validateForm();
        User user = AppState.getInstance().getUser();

        if (postDetails == null) {
            return false;
        }

        // Insert user to db
        System.out.println(postDetails.getID());
        Post newPost = DB.insertPost(postDetails.getID(), postDetails.getContent(), user.getUsername(),
                        postDetails.getLikes(), postDetails.getShares(), postDetails.getDateTime());

        newPost.displayDetails();

        if (newPost == null) {
            this.beforeContainer.getChildren().setAll(new CAlert("Something wrong happend!", "error"));
            return false;
        } else {
            this.beforeContainer.getChildren().setAll(new CAlert("Post has been successfully created!", "success"));
            resetTextFields();
        }
        return true;
    }

    public void onSecondaryBtnClicked(MouseEvent event) {
    }

    protected Post validateForm() {
        String id = this.textFieldElements.get("ID").getText().trim();
        String content = this.textFieldElements.get("Content").getText();
        String likes = this.textFieldElements.get("Likes").getText();
        String shares = this.textFieldElements.get("Shares").getText();
        String dateTime = this.textFieldElements.get("Date/Time").getText();

        // Validate & parse form
        this.resetTextFieldStyles();
        try {
            return NewPostFormController.parseForm(id, content, likes, shares, dateTime);
        } catch (InvalidFormException e) {
            // change border color of the text input to red
            this.setTextFieldErrorStyles(e.getErrors());
            this.beforeContainer.getChildren().setAll(new CAlert("Invalid post!", "error"));
            return null;
        }
    }

    private static Post parseForm(String id, String content, String likes, String shares, String dateTime)
                    throws InvalidFormException {
        Post post = new Post();
        HashMap<String, String> errors = new HashMap<String, String>();
        try {
            if (id.length() != 0) {
                post.setID(Parser.parseInt(id, 0));
            } else {
                post.setID(-1);
            }
        } catch (Exception e) {
            errors.put("ID", "ID must be a positive integer");
        }

        try {
            post.setContent(Parser.parseStr(content, true));
        } catch (Exception e) {
            errors.put("Content", "Content cannot be empty");
        }

        try {
            post.setLikes(Parser.parseInt(likes, 0));
        } catch (Exception e) {
            errors.put("Likes", "Likes must be positive integer");
        }

        try {
            post.setShares(Parser.parseInt(shares, 0));
        } catch (Exception e) {
            errors.put("Shares", "Shares must be positive integer");
        }

        try {
            post.setDateTime(Parser.parseDateTime(dateTime));
        } catch (Exception e) {
            errors.put("Date/Time", e.getMessage());
        }

        if (errors.size() > 0) {
            throw new InvalidFormException("Invalid form!", errors);
        }

        return post;
    }
}
