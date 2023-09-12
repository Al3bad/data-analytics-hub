package dev.alabbad.controllers;

import java.sql.SQLException;

import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.models.DB;

public class DeletePostFormController extends GetPostFormController {
    public DeletePostFormController() {
        super();
        this.primaryBtn.setText("Delete Post");
        this.btnGroup.getChildren().remove(1);
    }

    @Override
    protected void onSubmitHandler(int postId) throws PostNotFoundException, SQLException {
        DB.deletePost(postId);
        this.afterContainer.getChildren().setAll(new CAlert("The post has been successfully deleted!", "success"));
    }
}