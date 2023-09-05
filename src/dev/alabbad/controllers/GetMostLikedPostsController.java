package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.models.DB;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class GetMostLikedPostsController extends FormController {
    public GetMostLikedPostsController() {
        super(createTextFieldElements(), new Button("Get Most Liked Posts"));
    }

    public static LinkedHashMap<String, TextField> createTextFieldElements() {
        LinkedHashMap<String, TextField> textFieldElements = new LinkedHashMap<String, TextField>();
        textFieldElements.put("Author", new TextField());
        textFieldElements.put("Number of Posts", new TextField());
        return textFieldElements;
    }

    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        return true;
    }

    @Override
    protected void onSecondaryBtnClicked(MouseEvent event) {
    }

    protected void onSubmitHandler(int postId, String username) throws PostNotFoundException, SQLException {
        // Post post = DB.getPost(postId, username);
        // this.afterContainer.getChildren().setAll(new PostController(post));
    }
}
