package dev.alabbad.controllers;

import java.io.IOException;
import java.sql.SQLException;

import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.Post;
import dev.alabbad.utils.Parser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class GetPostFormController extends VBox {
    @FXML
    private TextField postId;

    @FXML
    protected Button primaryBtn;

    @FXML
    protected VBox container;

    public GetPostFormController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/get-post.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.primaryBtn.setText("Get Post");
    }

    @FXML
    private Boolean onGetPostBtnClicked(MouseEvent event) {
        try {
            int postId = Parser.parseInt(this.postId.getText(), 0);
            String username = AppState.getInstance().getUser().getUsername();
            // Get post from DB
            this.onSubmitHandler(postId, username);
            return true;
        } catch (PostNotFoundException e) {
            this.container.getChildren().setAll(new CAlert("Post not found!", "info"));
        } catch (SQLException e) {
            this.container.getChildren().setAll(new CAlert("Something wrong happends! [DB]", "error"));
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

    protected void onSubmitHandler(int postId, String username) throws PostNotFoundException, SQLException {
        Post post = DB.getPost(postId, username);
        this.container.getChildren().setAll(new PostController(post));
    }
}
