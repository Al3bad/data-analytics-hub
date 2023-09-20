package dev.alabbad.controllers;

import java.sql.SQLException;

import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.exceptions.UserNotFoundException;
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
        try {
            DB.deletePost(postId, AppState.getInstance().getUser().getUsername(),
                            AppState.getInstance().getUser().getUsername());

        } catch (UserNotFoundException e) {
            System.out.println("User not found!");
        }
        this.afterContainer.getChildren().setAll(new AlertView("The post has been successfully deleted!", "success"));
    }
}
