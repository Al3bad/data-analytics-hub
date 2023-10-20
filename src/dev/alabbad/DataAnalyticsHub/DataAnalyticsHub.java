package dev.alabbad.DataAnalyticsHub;

import java.sql.Connection;
import java.sql.SQLException;

import dev.alabbad.controllers.LoginFormController;
import dev.alabbad.exceptions.DatabaseConnectionException;
import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.interfaces.IDatabase;
import dev.alabbad.models.AdminUser;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.Model;
import dev.alabbad.views.DialogView;
import dev.alabbad.views.PortalScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Flow of the main GUI program
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class DataAnalyticsHub extends Application {
    private IDatabase<Connection> db;

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
        if (initModel("jdbc:sqlite:app.db") == false) {
            new DialogView("Error", "Program is not properly setup! Please contact the developer. [DB]", "OK");
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
     * @param url the filename for sqlite database
     * @return status
     */
    private Boolean initModel(String url) {
        try {
            // Connect to DB
            this.db = new DB();
            this.db.connect(url);
            Model.init(this.db.getConnection());
        } catch (DatabaseConnectionException e) {
            return false;
        }

        try {
            // NOTE: always make sure admin user exist in the system (this is for demo only)
            Model.getUserDao().insert(new AdminUser("admin", "admin", "Abdullah", "Alabbad"));
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
    private void cleanup() {
        System.out.println("Cleaning up!");
        // Close connection
        try {
            if (this.db != null && this.db.getConnection() != null) {
                this.db.getConnection().close();
            }
        } catch (SQLException e) {
        }
    }
}
