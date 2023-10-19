package dev.alabbad.controllers;

import java.sql.SQLException;

import dev.alabbad.exceptions.InvalidArgumentException;
import dev.alabbad.models.Model;

/**
 * Implementation of get most shared posts form
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class GetMostSharedPostsFormController extends GetMostLikedPostsFormController {
    public GetMostSharedPostsFormController() {
        super();
        this.primaryBtn.setText("Get Most Shared Posts");
    }

    /**
     * Get the most shared posts from the model
     *
     * @param author filter by the author
     * @param limit number of post
     * @throws InvalidArgumentException when an invalid argument is paassed to the
     * model
     * @throws SQLException
     */
    protected void onSubmitHandler(String author, int limit) throws SQLException, InvalidArgumentException {
        this.displayResult(author, Model.getPostDao().getSome("shares", author, limit));
    }
}
