package dev.alabbad.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import dev.alabbad.elements.ExtendedTextField;
import dev.alabbad.elements.PrimaryButton;
import dev.alabbad.elements.SecondaryButton;
import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.exceptions.UnauthorisedAction;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.Post;
import dev.alabbad.utils.Parser;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class GetPostFormController extends FormController {
    private Post retrievedPost;

    // TextField IDs & Labels
    private final static String POSTID = "Post ID";

    public GetPostFormController() {
        super(createTextFieldElements(), new PrimaryButton("Get Post"), new SecondaryButton("Export Post"));
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
        } catch (UnauthorisedAction e) {
            this.afterContainer.getChildren().setAll(new CAlert(e.getMessage(), "info"));
        } catch (Exception e) {
            this.afterContainer.getChildren()
                            .setAll(new CAlert("Invalid value! ID must be a positive integer!", "error"));
        }
        return false;
    }

    @Override
    protected void onSecondaryBtnClicked(MouseEvent event) {
        try {
            File fileLocation = chooseFileLocation();
            if (fileLocation != null) {
                this.exportPost(this.retrievedPost.getCSVFormat(), fileLocation);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found exception!");
        }
    }

    protected void onSubmitHandler(int postId) throws PostNotFoundException, SQLException, UnauthorisedAction {
        this.secondaryBtn.setDisable(true);
        this.retrievedPost = DB.getPost(postId);
        this.afterContainer.getChildren().setAll(new PostController(this.retrievedPost));
    }

    private File chooseFileLocation() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files ", "*.csv"));
        File file = fileChooser.showSaveDialog(AppState.getInstance().getStage());
        return file;
    }

    private void exportPost(String content, File file) throws FileNotFoundException {
        PrintWriter writer;
        writer = new PrintWriter(file);
        writer.println(content);
        writer.close();
    }
}
