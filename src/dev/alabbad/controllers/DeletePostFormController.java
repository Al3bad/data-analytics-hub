package dev.alabbad.controllers;

import java.sql.SQLException;

import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.Model;
import dev.alabbad.models.Post;
import dev.alabbad.views.AlertView;

public class DeletePostFormController extends GetPostFormController {
    public DeletePostFormController() {
        super();
        this.primaryBtn.setText("Delete Post");
        this.btnGroup.getChildren().remove(1);
    }

    @Override
    protected void onSubmitHandler(int postId) throws EntityNotFoundException, SQLException, UnauthorisedAction {
        try {
            Post post = Model.getPostDao().get(postId);
            Model.getPostDao().delete(post, AppState.getInstance().getUser());

        } catch (EntityNotFoundException e) {
            System.out.println("User not found!");
        }
        this.afterContainer.getChildren().setAll(new AlertView("The post has been successfully deleted!", "success"));
    }
}
