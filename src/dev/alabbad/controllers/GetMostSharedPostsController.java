package dev.alabbad.controllers;

import java.sql.SQLException;

import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.models.DB;

public class GetMostSharedPostsController extends GetMostLikedPostsController {
    public GetMostSharedPostsController() {
        super();
        this.primaryBtn.setText("Get Most Shared Posts");
    }

    protected void onSubmitHandler(int postId, String username) throws PostNotFoundException, SQLException {
        // Post post = DB.getPost(postId, username);
        // this.afterContainer.getChildren().setAll(new PostController(post));
    }
}
