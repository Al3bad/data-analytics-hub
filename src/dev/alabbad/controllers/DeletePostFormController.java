package dev.alabbad.controllers;

import java.sql.SQLException;

import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.views.AlertView;

public class DeletePostFormController extends GetPostFormController {
    public DeletePostFormController() {
        super();
        this.primaryBtn.setText("Delete Post");
        this.btnGroup.getChildren().remove(1);
    }

    @Override
    protected void onSubmitHandler(int postId) throws PostNotFoundException, SQLException, UnauthorisedAction {
        DB.deletePost(postId, AppState.getInstance().getUser().getUsername());
        this.afterContainer.getChildren().setAll(new AlertView("The post has been successfully deleted!", "success"));
    }
}
