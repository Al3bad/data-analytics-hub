package dev.alabbad.DataAnalyticsHub;

import java.sql.SQLException;

import dev.alabbad.controllers.LoginFormController;
import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.models.AdminUser;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.Model;
import dev.alabbad.views.PortalScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

/**
 * Flow of the main GUI program
 *
 * @author Abdullah Alabbad
 * @version 0.0.1
 */
public class DataAnalyticsHub extends Application {
    /**
     * Overriden method to start the javafx application
     *
     * @param stage primary stage of the program
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Init stage
        AppState.getInstance().setStage(stage);
        AppState.getInstance().getStage().setTitle("Data Analytics Hub");
        AppState.getInstance().getStage().setMinWidth(680);
        AppState.getInstance().getStage().setMinHeight(580);
        AppState.getInstance().switchScene(new Scene(new PortalScene(new LoginFormController())), false);
        // Show stage
        AppState.getInstance().getStage().show();
        // Problem with database;
        if (initModel("app.db") == false) {
            Dialog<ButtonType> dialog = new Dialog<ButtonType>();
            ButtonType okBtn = new ButtonType("OK", ButtonData.OK_DONE);
            dialog.setTitle("Error");
            dialog.setContentText("Program is not properly setup! Please contact the developer. [DB]");
            dialog.getDialogPane().getButtonTypes().add(okBtn);
            dialog.showAndWait();
            AppState.getInstance().getStage().close();
        }
    }

    /**
     * Ranner of the GUI application
     */
    public void run() {
        launch();
        cleanup();
    }

    /**
     * Initialise the database
     *
     * @param sqliteFilename the filename for sqlite database
     * @return status
     */
    public Boolean initModel(String sqliteFilename) {
        // Connect to DB
        try {
            Model.init(DB.connect(sqliteFilename));
        } catch (SQLException e) {
            return false;
        }
        // Create user table if it doesn't exist
        try {
            // NOTE: always make sure admin user exist in the system (this is for demo only)
            Model.getUserDao().insert(new AdminUser("admin", "admin", "Abdullah", "Alabbad"));
            // DB.insertUser("admin", "admin", "Abdullah", "Alabbad", true);
        } catch (SQLException e) {
            System.out.println("Admin user already exists");
        } catch (EntityNotFoundException e) {
            System.out.println("Something wrong happend! Please contact the developer!");
        }
        return true;
    }

    /**
     * Cleanup after quiting the application
     */
    public void cleanup() {
        System.out.println("Cleaning up!");
        // Close connection
        try {
            if (DB.getConnection() != null) {
                DB.getConnection().close();
            }
        } catch (SQLException e) {
        }
    }
}
