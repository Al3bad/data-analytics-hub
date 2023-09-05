package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.Post;
import dev.alabbad.utils.Parser;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class GetPostFormController extends FormController {
    public GetPostFormController() {
        super(createTextFieldElements(), new Button("Get Post"));
    }

    public static LinkedHashMap<String, TextField> createTextFieldElements() {
        LinkedHashMap<String, TextField> textFieldElements = new LinkedHashMap<String, TextField>();
        textFieldElements.put("postId", new TextField());
        return textFieldElements;
    }

    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        try {
            int postId = Parser.parseInt(this.textFieldElements.get("postId").getText(), 0);
            String username = AppState.getInstance().getUser().getUsername();
            // Get post from DB
            this.onSubmitHandler(postId, username);
            return true;
        } catch (PostNotFoundException e) {
            this.afterContainer.getChildren().setAll(new CAlert("Post not found!", "info"));
        } catch (SQLException e) {
            this.afterContainer.getChildren().setAll(new CAlert("Something wrong happends! [DB]", "error"));
        } catch (Exception e) {
            this.afterContainer.getChildren()
                            .setAll(new CAlert("Invalid value! ID must be a positive integer!", "error"));
        }
        return false;
    }

    @Override
    protected void onSecondaryBtnClicked(MouseEvent event) {
    }

    protected void onSubmitHandler(int postId, String username) throws PostNotFoundException, SQLException {
        Post post = DB.getPost(postId, username);
        this.afterContainer.getChildren().setAll(new PostController(post));
    }
}
