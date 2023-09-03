package dev.alabbad.controllers;

import java.util.HashMap;

import dev.alabbad.exceptions.InvalidFormException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.Post;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class NewPostFormController extends FormController {
    @FXML
    protected TextField content;

    @FXML
    protected TextField likes;

    @FXML
    protected TextField shares;

    @FXML
    protected TextField dateTime;

    public NewPostFormController() {
        super("/views/new-post-form.fxml");
        this.secondryBtn.setVisible(false);
        this.primaryBtn.setText("Post");
    }

    @FXML
    public Boolean onSubmitBtnClicked(MouseEvent event) {
        Post postDetails = this.validateForm();
        User user = AppState.getInstance().getUser();

        if (postDetails == null) {
            return false;
        }

        // Insert user to db
        Post newPost = DB.insertPost(postDetails.getContent(), user.getUsername(), postDetails.getLikes(),
                        postDetails.getShares(), postDetails.getDateTime());

        newPost.displayDetails();

        if (newPost == null) {
            this.statusContainer.getChildren().setAll(new CAlert("Something wrong happend!", "error"));
            return false;
        } else {
            this.statusContainer.getChildren().setAll(new CAlert("Post has been successfully created!", "success"));
            resetTextFields();
        }
        return true;
    }

    @FXML
    public void onCancelBtnClicked(MouseEvent event) {
        System.out.println("Add button clicked!");
    }

    protected Post validateForm() {
        String content = this.content.getText();
        String likes = this.likes.getText();
        String shares = this.shares.getText();
        String dateTime = this.dateTime.getText();

        // Validate & parse form
        this.resetTextFieldStyles();
        try {
            return NewPostFormController.parseForm(content, likes, shares, dateTime);
        } catch (InvalidFormException e) {
            // change border color of the text input to red
            this.setTextFieldErrorStyles(e.getErrors());
            this.statusContainer.getChildren().setAll(new CAlert("Invalid post!", "error"));
            return null;
        }
    }

    private static Post parseForm(String content, String likes, String shares, String dateTime)
                    throws InvalidFormException {
        Post post = new Post();
        HashMap<String, String> errors = new HashMap<String, String>();
        try {
            post.setContent(Parser.parseStr(content, true));
        } catch (Exception e) {
            errors.put("content", "Content cannot be empty");
        }

        try {
            post.setLikes(Parser.parseInt(likes, 0));
        } catch (Exception e) {
            errors.put("likes", "Likes must be positive integer");
        }

        try {
            post.setShares(Parser.parseInt(shares, 0));
        } catch (Exception e) {
            errors.put("shares", "Shares must be positive integer");
        }

        try {
            post.setDateTime(Parser.parseDateTime(dateTime));
        } catch (Exception e) {
            errors.put("dateTime", e.getMessage());
        }

        if (errors.size() > 0) {
            throw new InvalidFormException("Invalid form!", errors);
        }

        return post;
    }
}
