package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import dev.alabbad.exceptions.InvalidArgumentException;
import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.models.DB;
import dev.alabbad.models.Post;
import dev.alabbad.utils.Parser;
import dev.alabbad.views.AlertView;
import dev.alabbad.views.ExtendedTextField;
import dev.alabbad.views.PostView;
import dev.alabbad.views.PrimaryButton;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class GetMostLikedPostsController extends FormController {
    // TextField IDs & Labels
    protected final static String AUTHOR = "Author (Leave empty to get posts for all authors)";
    protected final static String NUM_OF_POSTS = "Number of Posts";

    public GetMostLikedPostsController() {
        super(createTextFieldElements(), new PrimaryButton("Get Most Liked Posts"));
    }

    public static LinkedHashMap<String, ExtendedTextField> createTextFieldElements() {
        LinkedHashMap<String, ExtendedTextField> textFieldElements = new LinkedHashMap<String, ExtendedTextField>();
        textFieldElements.put(AUTHOR, new ExtendedTextField<String>((val) -> Parser.parseStr(val, false, true)));
        textFieldElements.put(NUM_OF_POSTS, new ExtendedTextField<Integer>((val) -> Parser.parseInt(val, 0)));
        return textFieldElements;
    }

    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.afterContainer) == false) {
            return false;
        }

        try {
            String author = (String) this.textFieldElements.get(AUTHOR).getParsedVal();
            int limit = (int) this.textFieldElements.get(NUM_OF_POSTS).getParsedVal();
            // Get post from DB
            this.onSubmitHandler(author, limit);
            return true;
        } catch (InvalidArgumentException e) {
            this.afterContainer.getChildren().setAll(new AlertView(e.getMessage(), "error"));
        } catch (SQLException e) {
            this.afterContainer.getChildren().setAll(new AlertView("Something wrong happends! [DB]", "error"));
        } catch (Exception e) {
            this.afterContainer.getChildren()
                            .setAll(new AlertView("Invalid value! ID must be a positive integer!", "error"));
        }
        return true;
    }

    protected void onSubmitHandler(String author, int limit)
                    throws PostNotFoundException, SQLException, InvalidArgumentException {
        this.displayResult(author, DB.getPosts("likes", author, limit));
    }

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
