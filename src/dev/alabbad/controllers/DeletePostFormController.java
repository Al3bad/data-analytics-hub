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

    /**
     * Delete post by it's id from the model
     *
     * @param postId post id
     * @throws EntityNotFoundException when the post is not found
     * @throws UnauthorisedAction when the user attempt to delete someone else's
     * post
     * @throws SQLException
     */
    @Override
    protected void onSubmitHandler(int postId) throws EntityNotFoundException, SQLException, UnauthorisedAction {
        Post post = Model.getPostDao().get(postId);
        Model.getPostDao().delete(post, AppState.getInstance().getUser());
        this.afterContainer.getChildren().setAll(new AlertView("The post has been successfully deleted!", "success"));
    }
}
