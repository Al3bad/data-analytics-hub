package dev.alabbad.controllers;

import java.sql.SQLException;

import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.models.DB;

public class DeletePostFormController extends GetPostFormController {
    public DeletePostFormController() {
        super();
        this.primaryBtn.setText("Delete Post");
    }

    @Override
    protected void onSubmitHandler(int postId, String username) throws PostNotFoundException, SQLException {
        DB.deletePost(postId, username);
        this.afterContainer.getChildren().setAll(new CAlert("The post has been successfully deleted!", "success"));
    }
}
