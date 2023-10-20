package dev.alabbad.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import dev.alabbad.exceptions.EntityNotFoundException;
import dev.alabbad.models.AdminUser;
import dev.alabbad.models.AppState;
import dev.alabbad.models.Model;
import dev.alabbad.models.Post;
import dev.alabbad.models.User;
import dev.alabbad.models.VIPUser;
import dev.alabbad.utils.FileHandler;
import dev.alabbad.utils.Parser;
import dev.alabbad.views.DialogView;
import dev.alabbad.views.MainScene;
import dev.alabbad.views.PrimaryButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Dashboard view controller that normal users can upgrade their account and
 * non-normal users see the piechart and build import posts
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class DashboardController extends VBox {
    public DashboardController() {
        // construct view based on the logged user
        User loggedinUser = AppState.getInstance().getUser();
        if (loggedinUser instanceof VIPUser || loggedinUser instanceof AdminUser) {
            try {
                setupVIPUserView();
            } catch (SQLException e) {
                new DialogView("Error", "Couldn't get share distribution from model! Please contact the developer!",
                                "Ok");
            }
        } else {
            setupNormalUserView();
        }
        this.setAlignment(Pos.CENTER);
        this.setSpacing(8);
    }

    /**
     * Normal user view
     */
    private void setupNormalUserView() {
        Button vipButton = new PrimaryButton("Upgrade to VIP");
        vipButton.onMouseClickedProperty().set(event -> this.onVIPBtnClicked(event));
        this.getChildren().add(vipButton);
    }

    /**
     * VIP/Admin user view
     */
    private void setupVIPUserView() throws SQLException {
        // this.getChildren().add(new Label("You're a VIP user"));
        ArrayList<Integer> data = Model.getPostDao().getSharesDistribution();
        int sumRecords = data.stream().mapToInt(num -> num.intValue()).sum();
        if (sumRecords == 0) {
            this.getChildren().add(new Label("There is not posts in the system!"));
        } else {
            displayPieChart(data);
            displayNumOfSharePerCategory(data);
        }
        Button importPostsButton = new PrimaryButton("Import Posts");
        importPostsButton.onMouseClickedProperty().set(event -> {
            this.onImportBtnClicked(event);
            try {
                this.getChildren().setAll();
                this.setupVIPUserView();
            } catch (SQLException e) {
            }
        });
        this.getChildren().add(importPostsButton);
    }

    /**
     * Display pie chart of shares propotion
     *
     * @param data
     * @throws SQLException
     */
    private void displayPieChart(ArrayList<Integer> data) throws SQLException {
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

    /**
     * Dispaly number of post for each share category
     *
     * @param data
     */
    private void displayNumOfSharePerCategory(ArrayList<Integer> data) {
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

    /**
     * Upgrade normal user handler
     *
     * @param event mouse event
     */
    public void onVIPBtnClicked(MouseEvent event) {
        // display the dialgo for vip upgrade
        new DialogView("VIP Upgrade", "Would you like to subscribe to the application for a monthly fee of $0?", "Yes",
                        "Cancel", () -> {
                            try {
                                AppState.getInstance()
                                                .setUser(Model.getUserDao().upgrade(AppState.getInstance().getUser()));
                                AppState.getInstance().switchScene(new Scene(new MainScene()), true);
                            } catch (EntityNotFoundException e) {
                                new DialogView("User Not Found", "Couldn't upgrade user! Please contact the developer!",
                                                "OK");
                            } catch (SQLException e) {
                                new DialogView("DB Error", "Something wrong happend! Please contact the developer",
                                                "OK");
                            }
                        });
    }

    /**
     * Bulk import handler
     *
     * @param event mouse event
     */
    public void onImportBtnClicked(MouseEvent event) {
        try {
            File fileLocation = FileHandler.chooseFileForOpen("CSV files ", FileHandler.TYPE_CSV);
            if (fileLocation != null) {
                this.importPost(fileLocation);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found exception!");
        }
    }

    /**
     * Import only valid post from a CSV file
     *
     * @param file csf file that contains the post data
     * @throws FileNotFoundException
     */
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
                String[] fields = Parser.parseCSV(scan.nextLine(), expectedFieldsNum);
                int ID = Parser.parseInt(fields[0], 0);
                try {
                    // Skip if the there is an already existing post
                    Model.getPostDao().get(ID);
                    System.out.printf("Post with ID = %d already exist!\n", ID);
                } catch (EntityNotFoundException e) {
                    // parse fields
                    String content = fields[1];
                    String author = Parser.parseStr(fields[2]);
                    int likes = Parser.parseInt(fields[3], 0);
                    int shares = Parser.parseInt(fields[4], 0);
                    String dateTime = Parser.parseDateTime(fields[5]);
                    // create post obj
                    Model.getPostDao().insert(new Post(ID, content, author, likes, shares, dateTime));
                    validRowsCount++;
                }
            } catch (Exception e) {
                System.out.println("Expected " + expectedFieldsNum + " fields!");
            }
        }
        System.out.printf("%d valid posts has been loaded\n", validRowsCount);
        System.out.printf("%d invalid posts has been ignored\n", readRowsCount - validRowsCount);
    }

}
