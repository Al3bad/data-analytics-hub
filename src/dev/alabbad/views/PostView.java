package dev.alabbad.views;

import java.io.IOException;

import dev.alabbad.models.Post;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Post view to display post information
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class PostView extends VBox {
    @FXML
    private Text author;

    @FXML
    private Text id;

    @FXML
    private Label content;

    @FXML
    private Text likes;

    @FXML
    private Text shares;

    @FXML
    private Text dateTime;

    @FXML
    private VBox container;

    public PostView(Post post) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/post.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.author.setText("@" + post.getAuthor());
        this.id.setText(Integer.toString(post.getID()));
        this.content.setText(post.getContent());
        this.likes.setText(Integer.toString(post.getLikes()));
        this.shares.setText(Integer.toString(post.getShares()));
        this.dateTime.setText(post.getDateTime());
    }
}
