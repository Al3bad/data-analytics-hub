package dev.alabbad.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;

import dev.alabbad.exceptions.UserNotFoundException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;
import dev.alabbad.views.MainScene;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

public class AvatarController extends StackPane {
    public AvatarController() {
        final double radius = 28;
        // image
        Circle img = new Circle(radius);
        img.setStroke(Color.web("#0000001a"));
        img.setStrokeWidth(2.0);
        img.setStrokeType(StrokeType.INSIDE);
        if (AppState.getInstance().getUser().getProfileImg() != null) {
            img.setFill(pattern(new Image(AppState.getInstance().getUser().getProfileImg()), radius));
        } else {
            img.setFill(Color.web("#e1e1e1"));
        }
        Circle overlay = new Circle(radius);
        // overlay
        overlay.setOpacity(0);
        overlay.setCursor(Cursor.HAND);
        overlay.onMouseEnteredProperty().set(e -> this.onProfileImageMouseEnter(e, overlay));
        overlay.onMouseExitedProperty().set(e -> this.onProfileImageMouseExit(e, overlay));
        overlay.onMouseClickedProperty().set(e -> this.onProfileImageClicked(e));
        // setup element
        this.getChildren().addAll(img, overlay);
    }

    private void onProfileImageMouseEnter(MouseEvent event, Circle overlay) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), overlay);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(0.25);
        fadeTransition.setInterpolator(Interpolator.EASE_BOTH);
        fadeTransition.play();
    }

    private void onProfileImageMouseExit(MouseEvent event, Circle overlay) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), overlay);
        fadeTransition.setToValue(0.25);
        fadeTransition.setToValue(0);
        fadeTransition.setInterpolator(Interpolator.EASE_BOTH);
        fadeTransition.play();
    }

    private void onProfileImageClicked(MouseEvent event) {
        File fileLocation = chooseFileLocation();
        if (fileLocation != null) {
            try {
                InputStream img = new FileInputStream(fileLocation);
                User updatedUser = DB.updateUserProfileImg(AppState.getInstance().getUser().getUsername(), img);
                AppState.getInstance().setUser(updatedUser);
                AppState.getInstance().switchScene(new Scene(new MainScene()), true);
            } catch (IOException e) {
                System.out.println("File not read!");
            } catch (SQLException e) {
                System.out.println("SQLException");
            } catch (UserNotFoundException e) {
                System.out.println("UserNotFoundException");
            }
        }
    }

    private File chooseFileLocation() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .addAll(new ExtensionFilter("Image Files ", Arrays.asList("*.jpeg", "*.jpg", "*.png")));
        File file = fileChooser.showOpenDialog(AppState.getInstance().getStage());
        return file;
    }

    ImagePattern pattern(Image img, double radius) {
        // Preserving aspect ratio of an image
        // reference: https://stackoverflow.com/a/67612795/10823489
        double hRad = radius;
        double vRad = radius;
        if (img.getWidth() != img.getHeight()) {
            double ratio = img.getWidth() / img.getHeight();
            if (ratio > 1) {
                // Width is longer, left anchor is outside
                hRad = radius * ratio;
            } else {
                // Height is longer, top anchor is outside
                vRad = radius / ratio;
            }
        }
        return new ImagePattern(img, -hRad, -vRad, 2 * hRad, 2 * vRad, false);
    }
}
