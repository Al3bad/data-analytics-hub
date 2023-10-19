package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import dev.alabbad.exceptions.InvalidArgumentException;
import dev.alabbad.models.Model;
import dev.alabbad.models.Post;
import dev.alabbad.utils.Parser;
import dev.alabbad.views.AlertView;
import dev.alabbad.views.ExtendedTextField;
import dev.alabbad.views.PostView;
import dev.alabbad.views.PrimaryButton;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * Implementation of get most liked posts form
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class GetMostLikedPostsFormController extends FormController {
    // TextField IDs & Labels
    protected final static String AUTHOR = "Author (Leave empty to get posts for all authors)";
    protected final static String NUM_OF_POSTS = "Number of Posts";

    public GetMostLikedPostsFormController() {
        super(createTextFieldElements(), new PrimaryButton("Get Most Liked Posts"));
    }

    /**
     * Add the text fields for this form
     *
     * @return linked hash map containing the text field elements
     */
    public static LinkedHashMap<String, ExtendedTextField> createTextFieldElements() {
        LinkedHashMap<String, ExtendedTextField> textFieldElements = new LinkedHashMap<String, ExtendedTextField>();
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
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.afterContainer) == false) {
            return false;
        }

        try {
            String author = (String) this.textFieldElements.get(AUTHOR).getParsedVal();
            int limit = (int) this.textFieldElements.get(NUM_OF_POSTS).getParsedVal();
            this.onSubmitHandler(author, limit);
        } catch (InvalidArgumentException e) {
            this.afterContainer.getChildren().setAll(new AlertView(e.getMessage(), "error"));
        } catch (SQLException e) {
            this.afterContainer.getChildren()
                            .setAll(new AlertView("Something wrong happend! Please contact the developer", "error"));
        }
        return true;
    }

    /**
     * Get the most liked posts from the model
     *
     * @param author filter by the author
     * @param limit number of post
     * @throws InvalidArgumentException when an invalid argument is paassed to the
     * model
     * @throws SQLException
     */
    protected void onSubmitHandler(String author, int limit) throws SQLException, InvalidArgumentException {
        this.displayResult(author, Model.getPostDao().getSome("likes", author, limit));
    }

    /**
     * Display the posts in the view
     *
     * @param author
     * @param posts
     */
    protected void displayResult(String author, ArrayList<Post> posts) {
        if (posts.size() == 0 && author.length() != 0) {
            this.afterContainer.getChildren().setAll(new AlertView("The specified author was not found!", "info"));
        } else if (posts.size() == 0) {
            this.afterContainer.getChildren().setAll(new AlertView("There no posts added in the system yet!", "info"));
        } else {
            this.afterContainer.getChildren().setAll();
            for (Post post : posts) {
                VBox postComponent = new PostView(post);
                VBox.setMargin(postComponent, new Insets(0, 0, 12, 0));
                this.afterContainer.getChildren().add(postComponent);
            }
        }
    }
}
