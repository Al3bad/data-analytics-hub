package dev.alabbad.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.alabbad.models.AppState;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * A collection of static methods to handle files in the system
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class FileHandler {
    public static final ArrayList<String> TYPE_CSV = new ArrayList<>(Arrays.asList("*.csv"));
    public static final ArrayList<String> TYPE_IMG = new ArrayList<>(Arrays.asList("*.jpeg", "*.jpg", "*.png"));

    /**
     * Create file chooser object and set externsion filter
     *
     * @param label
     * @param filetypes list of allowed file types
     * @return file chooser object
     */
    private static FileChooser chooseFile(String label, List<String> filetypes) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter(label, filetypes));
        return fileChooser;
    }

    /**
     * Open dialog
     *
     * @param label
     * @param filetypes list of allowed file types
     * @return File object
     */
    public static File chooseFileForOpen(String label, List<String> filetypes) {
        File file = chooseFile(label, filetypes).showOpenDialog(AppState.getInstance().getStage());
        return file;
    }

    /**
     * Save dialog
     *
     * @param label
     * @param filetypes list of file types
     * @return File object
     */
    public static File chooseFileForSave(String label, List<String> filetypes) {
        File file = chooseFile(label, filetypes).showSaveDialog(AppState.getInstance().getStage());
        return file;
    }

    /**
     * Write data in a the specified file
     *
     * @param data
     * @param file
     * @throws FileNotFoundException when error occre during writing
     */
    public static void writeToFile(String data, File file) throws FileNotFoundException {
        PrintWriter writer;
        writer = new PrintWriter(file);
        writer.println(data);
        writer.close();
    }
}
