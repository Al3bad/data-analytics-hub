import java.sql.SQLException;
import java.util.HashMap;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Flow of the main program
 *
 * @author Abdullah Alabbad
 * @version 0.0.1
 */
public class DataAnalyticsHub extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = new CRoot().getComponent();
        Scene scene = new Scene(root, 600, 300);
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
