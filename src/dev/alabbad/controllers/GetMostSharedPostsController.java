package dev.alabbad.controllers;

import java.sql.SQLException;

import dev.alabbad.exceptions.InvalidArgumentException;
import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.models.DB;

public class GetMostSharedPostsController extends GetMostLikedPostsController {
    public GetMostSharedPostsController() {
        super();
        this.primaryBtn.setText("Get Most Shared Posts");
    }

    protected void onSubmitHandler(String author, int limit)
                    throws PostNotFoundException, SQLException, InvalidArgumentException {
        this.displayResult(author, DB.getPosts("shares", author, limit));
    }
}
