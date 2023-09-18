package dev.alabbad.DataAnalyticsHub;

import dev.alabbad.controllers.*;
import dev.alabbad.views.*;
import dev.alabbad.models.*;

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
        AnchorPane portalScene = new PortalScene(new LoginFormController());
        Scene scene = new Scene(portalScene);
        // Setting the title to Stage.
        stage.setTitle("Data Analytics Hub");
        // Setting min width and height to stage
        AppState.getInstance().getStage().setMinWidth(minWidth);
        AppState.getInstance().getStage().setMinHeight(minHeight);
        AppState.getInstance().switchScene(scene, false);
        // Displaying the contents of the stage
        AppState.getInstance().getStage().show();
    }

    public void run(String sqliteFilename) {
        // Connect to DB
        DB.connect(sqliteFilename);
        // Create user table if it doesn't exist
        DB.createUserTable();
        DB.createPostTable();
        launch();
        // Close connection
        try {
            DB.getConnection().close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
