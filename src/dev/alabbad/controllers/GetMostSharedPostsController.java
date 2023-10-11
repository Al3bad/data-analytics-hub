package dev.alabbad.controllers;

import java.sql.SQLException;

import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.exceptions.InvalidArgumentException;
import dev.alabbad.models.Model;

public class GetMostSharedPostsController extends GetMostLikedPostsController {
    public GetMostSharedPostsController() {
        super();
        this.primaryBtn.setText("Get Most Shared Posts");
    }

    protected void onSubmitHandler(String author, int limit)
            throws EntityNotFoundException, SQLException, InvalidArgumentException {
        this.displayResult(author, Model.getPostDao().getSome("shares", author, limit));
    }
}
