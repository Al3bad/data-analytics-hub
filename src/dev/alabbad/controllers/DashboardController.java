package dev.alabbad.controllers;

import java.sql.SQLException;
import java.util.Optional;

import dev.alabbad.exceptions.UserNotFoundException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.VIPUser;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class DashboardController extends VBox {
    public DashboardController() {
        // construct view based on the logged user
        if (AppState.getInstance().getUser() instanceof VIPUser) {
            setupVIPUserView();
        } else {
            setupNormalUserView();
        }
    }

    private void setupNormalUserView() {
        Button vipButton = new Button("Upgrade to VIP");
        vipButton.setId("vip-button");
        vipButton.setMaxWidth(Double.MAX_VALUE);
        vipButton.getStylesheets().add("/css/button.css");
        vipButton.getStyleClass().add("button");
        vipButton.getStyleClass().add("primary");
        vipButton.onMouseClickedProperty().set(event -> this.onPrimaryBtnClicked(event));
        this.getChildren().add(vipButton);
    }

    private void setupVIPUserView() {
        this.getChildren().add(new Label("You're a VIP user"));
    }

    public void onPrimaryBtnClicked(MouseEvent event) {
        // display the dialgo for vip upgrade
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.setTitle("VIP Upgrade");
        dialog.setContentText("Would you like to subscribe to the application for a monthly fee of $0?");
        ButtonType upgradeBtn = new ButtonType("Upgrade", ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(upgradeBtn);
        dialog.getDialogPane().getButtonTypes().add(cancelBtn);
        Optional<ButtonType> result = dialog.showAndWait();

        // take action based on result
        if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
            try {
                AppState.getInstance().setUser(DB.upgradeUser(AppState.getInstance().getUser()));
                AppState.getInstance().switchScene(new Scene(new MainSceneController()), true);
            } catch (SQLException e) {
                // TODO: handle exception
                System.out.println("SQLite exception!");
            } catch (UserNotFoundException e) {
                // TODO: handle exception
                System.out.println("User not found exception");
            }
        }
    }
}
