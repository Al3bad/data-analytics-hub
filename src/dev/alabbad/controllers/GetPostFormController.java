package dev.alabbad.controllers;

import java.io.IOException;

import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.Post;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class GetPostFormController extends VBox {
    @FXML
    private TextField postId;

    @FXML
    private VBox container;

    public GetPostFormController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/get-post.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private Boolean onGetPostBtnClicked(MouseEvent event) {
        try {
            int postId = Parser.parseInt(this.postId.getText(), 0);
            User user = AppState.getInstance().getUser();
            // Get post from DB
            Post post = DB.getPost(postId, user.getUsername());
            this.container.getChildren().setAll(new PostController(post));
            return true;
        } catch (PostNotFoundException e) {
            this.container.getChildren().setAll(new CAlert("Post not found!", "info"));
        } catch (Exception e) {
            this.container.getChildren().setAll(new CAlert("Invalid value! ID must be a positive integer!", "error"));
        }
        return false;
    }

    @FXML
    protected void onKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            this.onGetPostBtnClicked(null);
        }
    }

}
