package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.exceptions.InvalidArgumentException;
import dev.alabbad.interfaces.IInputControl;
import dev.alabbad.models.Model;
import dev.alabbad.models.Post;
import dev.alabbad.utils.Parser;
import dev.alabbad.views.AlertView;
import dev.alabbad.views.ExtendedTextField;
import dev.alabbad.views.PostView;
import dev.alabbad.views.PrimaryButton;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * Implementation of get most liked posts form
 *
 * @author Abdullah Alabbad
 * @version 1.0.1
 */
public class GetMostLikedPostsFormController extends FormController {
    // TextField IDs & Labels
    protected final static String AUTHOR = "Author (Leave empty to get posts for all authors)";
    protected final static String NUM_OF_POSTS = "Number of Posts";

    public GetMostLikedPostsFormController() {
        super(createInputElements(), new PrimaryButton("Get Most Liked Posts"));
    }

    /**
     * Add the text fields for this form
     *
     * @return linked hash map containing the text field elements
     */
    public static LinkedHashMap<String, Control> createInputElements() {
        LinkedHashMap<String, Control> textFieldElements = new LinkedHashMap<String, Control>();
        textFieldElements.put(AUTHOR, new ExtendedTextField<String>((val) -> Parser.parseStr(val, false, true)));
        textFieldElements.put(NUM_OF_POSTS, new ExtendedTextField<Integer>((val) -> Parser.parseInt(val, 0)));
        return textFieldElements;
    }

    /**
     * Call the form submission handler
     *
     * @param event moust event
     * @return true if the form is valid, false, otherwise
     */
    @Override
    protected void onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.afterContainer) == false) {
            return;
        }

        try {
            String author = (String) ((IInputControl) this.inputControlElements.get(AUTHOR)).getParsedVal();
            int limit = (int) ((IInputControl) this.inputControlElements.get(NUM_OF_POSTS)).getParsedVal();
            this.onSubmitHandler(author, limit);
        } catch (InvalidArgumentException e) {
            this.afterContainer.getChildren().setAll(new AlertView(e.getMessage(), "error"));
        } catch (SQLException e) {
            this.afterContainer.getChildren()
                    .setAll(new AlertView("Something wrong happend! Please contact the developer", "error"));
        }
    }

    /**
     * Get the most liked posts from the model
     *
     * @param author filter by the author
     * @param limit  number of post
     * @throws InvalidArgumentException when an invalid argument is paassed to the
     *                                  model
     * @throws SQLException
     */
    protected void onSubmitHandler(String author, int limit) throws SQLException, InvalidArgumentException {
        this.displayResult(author, Model.getPostDao().getSome("likes", author, limit));
    }

    /**
     * Display the result based on the returned information
     *
     * @param author
     * @param posts
     */
    protected void displayResult(String author, ArrayList<Post> posts) throws SQLException {
        if (author != null) {
            // check if the specified author exist
            try {
                Model.getUserDao().get(author);
                if (posts.size() == 0) {
                    // check if there are any posts posted by the specified author
                    this.afterContainer.getChildren()
                            .setAll(new AlertView("There no posts added by the specified author!", "info"));
                } else {
                    this.displayPosts(posts);
                }
            } catch (EntityNotFoundException e) {
                this.afterContainer.getChildren().setAll(new AlertView("The specified author was not found!", "info"));
            }
            return;
        } else if (author == null && posts.size() == 0) {
            // check if there are any posts in the system
            this.afterContainer.getChildren().setAll(new AlertView("There no posts in the system!", "info"));
            return;
        }
        displayPosts(posts);
    }

    /**
     * Display the posts in the view
     *
     * @param posts
     */
    private void displayPosts(ArrayList<Post> posts) {
        this.afterContainer.getChildren().setAll();
        for (Post post : posts) {
            VBox postComponent = new PostView(post);
            VBox.setMargin(postComponent, new Insets(0, 0, 12, 0));
            this.afterContainer.getChildren().add(postComponent);
        }

    }
}
