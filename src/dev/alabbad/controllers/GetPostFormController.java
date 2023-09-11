package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

import dev.alabbad.elements.ExtendedTextField;
import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.models.DB;
import dev.alabbad.models.Post;
import dev.alabbad.utils.Parser;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class GetPostFormController extends FormController {
    // TextField IDs & Labels
    private final static String POSTID = "Post ID";

    public GetPostFormController() {
        super(createTextFieldElements(), new Button("Get Post"), new Button("Export Post"));
        this.secondaryBtn.setDisable(true);
    }

    public static LinkedHashMap<String, ExtendedTextField> createTextFieldElements() {
        LinkedHashMap<String, ExtendedTextField> textFieldElements = new LinkedHashMap<String, ExtendedTextField>();
        textFieldElements.put(POSTID, new ExtendedTextField<Integer>((val) -> Parser.parseInt(val, 0)));
        return textFieldElements;
    }

    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.afterContainer) == false) {
            return false;
        }

        try {
            int postId = (int) this.textFieldElements.get(POSTID).getParsedVal();
            // Get post from DB
            this.onSubmitHandler(postId);
            this.secondaryBtn.setDisable(false);
            return true;
        } catch (PostNotFoundException e) {
            this.afterContainer.getChildren().setAll(new CAlert("Post not found!", "info"));
        } catch (SQLException e) {
            this.afterContainer.getChildren().setAll(new CAlert("Something wrong happends! [DB]", "error"));
        } catch (Exception e) {
            this.afterContainer.getChildren()
                            .setAll(new CAlert("Invalid value! ID must be a positive integer!", "error"));
        }
        return false;
    }

    @Override
    protected void onSecondaryBtnClicked(MouseEvent event) {
        // TODO: export post
    }

    protected void onSubmitHandler(int postId) throws PostNotFoundException, SQLException {
        this.secondaryBtn.setDisable(true);
        Post post = DB.getPost(postId);
        this.afterContainer.getChildren().setAll(new PostController(post));
    }
}
