package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

import dev.alabbad.elements.ExtendedTextField;
import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.utils.Parser;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class GetMostLikedPostsController extends FormController {
    // TextField IDs & Labels
    protected final static String AUTHOR = "Author";
    protected final static String NUM_OF_POSTS = "Number of Posts";

    public GetMostLikedPostsController() {
        super(createTextFieldElements(), new Button("Get Most Liked Posts"));
    }

    public static LinkedHashMap<String, ExtendedTextField> createTextFieldElements() {
        LinkedHashMap<String, ExtendedTextField> textFieldElements = new LinkedHashMap<String, ExtendedTextField>();
        textFieldElements.put("Author", new ExtendedTextField<String>((val) -> Parser.parseStr(val, true)));
        textFieldElements.put("Number of Posts", new ExtendedTextField<String>((val) -> Parser.parseStr(val, true)));
        return textFieldElements;
    }

    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        return true;
    }

    protected void onSubmitHandler(int postId, String username) throws PostNotFoundException, SQLException {
        // Post post = DB.getPost(postId, username);
        // this.afterContainer.getChildren().setAll(new PostController(post));
    }
}
