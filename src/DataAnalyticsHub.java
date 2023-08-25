/**
 * Flow of the main program
 *
 * @author Abdullah Alabbad
 * @version 0.0.1
 */
public class DataAnalyticsHub {
    public void run(String sqliteFilename) {
        DB.connect(sqliteFilename);
    }
}
