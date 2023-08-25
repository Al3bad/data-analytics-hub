import java.sql.SQLException;
import java.util.HashMap;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.*;

/**
 * Flow of the main program
 *
 * @author Abdullah Alabbad
 * @version 0.0.1
 */
public class DataAnalyticsHub extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Register pane
        FlowPane pane = new FlowPane(Orientation.VERTICAL, 5, 5);
        pane.setPadding(new Insets(11, 12, 13, 14));

        Text text1 = new Text(20, 20, "Create New User");
        text1.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.ITALIC, 15));
        pane.getChildren().add(text1);

        pane.getChildren().addAll(new Label("Username"), new TextField(), new Label("Password"), new TextField(),
                        new Label("First Name:"), new TextField(), new Label("Last Name:"), new TextField(),
                        new Button("Register"));

        // Creating a Scene by passing the group object, height and width
        Scene scene = new Scene(pane, 600, 300);

        // Setting the title to Stage.
        primaryStage.setTitle("Data Analytics Hub");
        // Adding the scene to Stage
        primaryStage.setScene(scene);
        // Displaying the contents of the stage
        primaryStage.show();
    }

    public void run(String sqliteFilename) {
        // Connect to DB
        DB.connect(sqliteFilename);
        // Create user table if it doesn't exist
        DB.createUserTable();
        // Get all users in table
        HashMap<String, User> users = DB.getAllUsers();
        for (User user : users.values()) {
            user.displayDetails();
        }
        launch();
        // Close connection
        try {
            DB.conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
