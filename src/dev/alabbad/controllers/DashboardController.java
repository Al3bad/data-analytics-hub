package dev.alabbad.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import dev.alabbad.exceptions.PostNotFoundException;
import dev.alabbad.exceptions.UserNotFoundException;
import dev.alabbad.models.AdminUser;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;
import dev.alabbad.models.VIPUser;
import dev.alabbad.utils.Parser;
import dev.alabbad.views.MainScene;
import dev.alabbad.views.PrimaryButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class DashboardController extends VBox {
    public DashboardController() {
        // construct view based on the logged user
        User loggedinUser = AppState.getInstance().getUser();
        if (loggedinUser instanceof VIPUser || loggedinUser instanceof AdminUser) {
            try {
                setupVIPUserView();
            } catch (SQLException e) {
                // TODO: handle error when
                System.out.println("Couldn't get data of share distribution!");
            }
        } else {
            setupNormalUserView();
        }
        this.setAlignment(Pos.CENTER);
        this.setSpacing(8);
    }

    private void setupNormalUserView() {
        Button vipButton = new PrimaryButton("Upgrade to VIP");
        vipButton.onMouseClickedProperty().set(event -> this.onVIPBtnClicked(event));
        this.getChildren().add(vipButton);
    }

    private void setupVIPUserView() throws SQLException {
        // this.getChildren().add(new Label("You're a VIP user"));
        ArrayList<Integer> data = DB.getSharesDistribution();
        int sumRecords = data.stream().mapToInt(num -> num.intValue()).sum();
        if (sumRecords == 0) {
            this.getChildren().add(new Label("There is not posts in the system!"));
        } else {
            drawPieChart(data);
            displayValues(data);
        }
        Button importPostsButton = new PrimaryButton("Import Posts");
        importPostsButton.onMouseClickedProperty().set(event -> this.onImportBtnClicked(event));
        this.getChildren().add(importPostsButton);
    }

    private void displayValues(ArrayList<Integer> data) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(16);

        // node, col, row, colSpan, rowSpan
        gridPane.add(new Label("0-99 Shares"), 0, 0, 1, 1);
        gridPane.add(new Label(":  " + data.get(0).toString() + " posts"), 1, 0, 1, 1);

        gridPane.add(new Label("100-999 Shares"), 0, 1, 1, 1);
        gridPane.add(new Label(":  " + data.get(1).toString() + " posts"), 1, 1, 1, 1);

        gridPane.add(new Label("> 1000 Shares"), 0, 2, 1, 1);
        gridPane.add(new Label(":  " + data.get(2).toString() + " posts"), 1, 2, 1, 1);
        this.getChildren().add(gridPane);
    }

    public void onVIPBtnClicked(MouseEvent event) {
        // display the dialgo for vip upgrade
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.setTitle("VIP Upgrade");
        dialog.setContentText("Would you like to subscribe to the application for a monthly fee of $0?");
        ButtonType upgradeBtn = new ButtonType("Yes", ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(upgradeBtn);
        dialog.getDialogPane().getButtonTypes().add(cancelBtn);
        Optional<ButtonType> result = dialog.showAndWait();

        // take action based on result
        if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
            try {
                AppState.getInstance().setUser(DB.upgradeUser(AppState.getInstance().getUser().getUsername()));
                AppState.getInstance().switchScene(new Scene(new MainScene()), true);
            } catch (SQLException e) {
                // TODO: handle exception
                System.out.println("SQLite exception!");
            } catch (UserNotFoundException e) {
                // TODO: handle exception
                System.out.println("User not found exception");
            }
        }
    }

    public void onImportBtnClicked(MouseEvent event) {
        try {
            File fileLocation = chooseFileLocation();
            if (fileLocation != null) {
                this.importPost(fileLocation);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found exception!");
        }
    }

    private void drawPieChart(ArrayList<Integer> data) throws SQLException {
        // Preparing ObservbleList object
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        if (data.get(0) > 0) {
            pieData.add(new PieChart.Data("0 - 99 Shares", data.get(0)));
        }
        if (data.get(1) > 0) {
            pieData.add(new PieChart.Data("100 - 999 Shares", data.get(1)));
        }
        if (data.get(2) > 0) {
            pieData.add(new PieChart.Data("> 1000 Shares", data.get(2)));
        }

        // Creating a Pie chart
        PieChart pieChart = new PieChart(pieData);
        pieChart.setData(pieData);
        pieChart.setTitle("Shares Distribution");
        pieChart.setLabelLineLength(32);
        pieChart.setStartAngle(90);
        pieChart.setLegendVisible(false);
        this.getChildren().add(pieChart);
    }

    private File chooseFileLocation() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files ", "*.csv"));
        File file = fileChooser.showOpenDialog(AppState.getInstance().getStage());
        return file;
    }

    private void importPost(File file) throws FileNotFoundException {
        // create scanner for the file
        int expectedFieldsNum = 6;
        int readRowsCount = 0;
        int validRowsCount = 0;
        Scanner scan = new Scanner(file);
        // skip first row (headers)
        if (scan.hasNextLine()) {
            scan.nextLine();
        }
        // process each row
        while (scan.hasNextLine()) {
            try {
                readRowsCount++;
                String[] fields = parseCSV(scan.nextLine(), expectedFieldsNum);
                int ID = Parser.parseInt(fields[0], 0);
                try {
                    // Skip if the there is an already existing post
                    DB.getPost(ID);
                    System.out.printf("Post with ID = %d already exist!\n", ID);
                } catch (PostNotFoundException e) {
                    // parse fields
                    String content = fields[1];
                    String author = Parser.parseStr(fields[2]);
                    int likes = Parser.parseInt(fields[3], 0);
                    int shares = Parser.parseInt(fields[4], 0);
                    String dateTime = Parser.parseDateTime(fields[5]);
                    // create post obj
                    DB.insertPost(ID, content, author, likes, shares, dateTime);
                    validRowsCount++;
                }
            } catch (Exception e) {
                System.out.println("Expected " + expectedFieldsNum + " fields!");
            }
        }
        System.out.printf("%d valid posts has been loaded\n", validRowsCount);
        System.out.printf("%d invalid posts has been ignored\n", readRowsCount - validRowsCount);
    }

    /**
     * Parse CSV string
     *
     * @param str the CSV string to be parsed
     * @param expectedFieldsNum the number of expected fields
     * @return array of strings
     * @throws Exception
     */
    public static String[] parseCSV(String str, int expectedFieldsNum) throws Exception {
        String[] fields = str.split(",");

        if (fields.length != expectedFieldsNum) {
            throw new Exception("Invalid number of fields");
        }

        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].strip();
        }
        return fields;
    }

}
