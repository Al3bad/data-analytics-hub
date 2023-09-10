package dev.alabbad.DataAnalyticsHub;

import dev.alabbad.controllers.LoginFormController;
import dev.alabbad.controllers.CPortalScene;
import dev.alabbad.controllers.FormController;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

/**
 * Flow of the main program
 *
 * @author Abdullah Alabbad
 * @version 0.0.1
 */
public class DataAnalyticsHub extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        AppState.getInstance().setStage(stage);
        double minWidth = 680;
        double minHeight = 480;
        LinkedHashMap<String, TextField> textFieldElements = new LinkedHashMap<String, TextField>();
        textFieldElements.put("username", new TextField());
        textFieldElements.put("password", new PasswordField());
        AnchorPane portalScene = new CPortalScene(new LoginFormController());
        Scene scene = new Scene(portalScene, minWidth, minHeight);
        // Setting the title to Stage.
        stage.setTitle("Data Analytics Hub");
        // Setting min width and height to stage
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
        // Adding the scene to Stage
        stage.setScene(scene);
        // Displaying the contents of the stage
        stage.show();
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
            DB.getConnection().close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
