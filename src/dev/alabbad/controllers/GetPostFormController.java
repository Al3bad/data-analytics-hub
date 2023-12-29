package dev.alabbad.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.interfaces.IInputControl;
import dev.alabbad.models.Model;
import dev.alabbad.models.Post;
import dev.alabbad.utils.FileHandler;
import dev.alabbad.utils.Parser;
import dev.alabbad.views.AlertView;
import dev.alabbad.views.ExtendedTextField;
import dev.alabbad.views.PostView;
import dev.alabbad.views.PrimaryButton;
import dev.alabbad.views.SecondaryButton;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;

/**
 * Implementation of get post form
 *
 * @author Abdullah Alabbad
 * @version 1.0.1
 */
public class GetPostFormController extends FormController {
    private Post retrievedPost;

    // TextField IDs & Labels
    private final static String POSTID = "Post ID";

    public GetPostFormController() {
        super(createInputElements(), new PrimaryButton("Get Post"), new SecondaryButton("Export Post"));
        this.secondaryBtn.setDisable(true);
    }

    /**
     * Add the text fields for this form
     *
     * @return linked hash map containing the text field elements
     */
    public static LinkedHashMap<String, Control> createInputElements() {
        LinkedHashMap<String, Control> textFieldElements = new LinkedHashMap<String, Control>();
        textFieldElements.put(POSTID, new ExtendedTextField<Integer>((val) -> Parser.parseInt(val, 0)));
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
            int postId = (int) ((IInputControl) this.inputControlElements.get(POSTID)).getParsedVal();
            // Get post from DB
            this.onSubmitHandler(postId);
            this.secondaryBtn.setDisable(false);
        } catch (EntityNotFoundException e) {
            this.afterContainer.getChildren().setAll(new AlertView("Post not found!", "info"));
        } catch (SQLException e) {
            this.afterContainer.getChildren().setAll(new AlertView("Something wrong happends! [DB]", "error"));
        } catch (UnauthorisedAction e) {
            this.afterContainer.getChildren().setAll(new AlertView(e.getMessage(), "info"));
        }
    }

    /**
     * Secondary button handler - Export post
     *
     * @param event moust event
     */
    @Override
    protected void onSecondaryBtnClicked(MouseEvent event) {
        try {
            File fileLocation = FileHandler.chooseFileForSave("CSV Files ", FileHandler.TYPE_CSV);
            if (fileLocation != null) {
                FileHandler.writeToFile(this.retrievedPost.toString(), fileLocation);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found exception!");
        }
    }

    /**
     * Get post by it's id from the model
     *
     * @param postId post id
     * @throws EntityNotFoundException when the post is not found
     * @throws SQLException
     */
    protected void onSubmitHandler(int postId) throws SQLException, UnauthorisedAction, EntityNotFoundException {
        this.secondaryBtn.setDisable(true);
        this.retrievedPost = Model.getPostDao().get(postId);
        this.afterContainer.getChildren().setAll(new PostView(this.retrievedPost));
    }
}
