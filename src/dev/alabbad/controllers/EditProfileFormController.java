package dev.alabbad.controllers;

import dev.alabbad.exceptions.UserNotFoundException;
import dev.alabbad.models.AppState;
import dev.alabbad.models.DB;
import dev.alabbad.models.User;
import dev.alabbad.utils.Parser;
import dev.alabbad.views.AlertView;
import dev.alabbad.views.ExtendedPasswordField;
import dev.alabbad.views.MainScene;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

public class EditProfileFormController extends SignupFormController {
    // TextField IDs & Labels
    protected final static String CURRENT_PASSWORD = "Current Password";
    protected final static String NEW_PASSWORD = "New Password";

    public EditProfileFormController() {
        super();
        this.textFieldElements.put(NEW_PASSWORD,
                        new ExtendedPasswordField<String>((val) -> Parser.parseStr(val, false)));
        setupForm();
        setupAvatar();
        this.setAlignment(Pos.CENTER);

        // TODO: make sure that user creds are valid before updating
        // TODO: handle some errors properly

        User loggedinUser = AppState.getInstance().getUser();
        this.textFieldElements.get(USERNAME).setText(loggedinUser.getUsername());
        this.textFieldElements.get(FNAME).setText(loggedinUser.getFirstName());
        this.textFieldElements.get(LNAME).setText(loggedinUser.getLastName());
        this.textFieldElements.get(PASSWORD).setText("");
        // Replace the label from "Password" to "Current Password"
        VBox inputContainer = (VBox) this.textFieldElements.get(PASSWORD).getParent();
        Label label = (Label) inputContainer.getChildren().get(0);
        label.setText(CURRENT_PASSWORD);
        this.primaryBtn.setText("Edit");
    }

    private void setupAvatar() {
        StackPane avatarContainer = new StackPane();
        Circle avatar = new Circle(32);
        Circle overlay = new Circle(32);
        avatar.setFill(Color.web("#e1e1e1"));
        avatar.setStroke(Color.web("#0000001a"));
        avatar.setStrokeWidth(2.0);
        avatar.setStrokeType(StrokeType.INSIDE);
        if (AppState.getInstance().getUser().getProfileImg() != null) {
            // avatar.setFill(new ImagePattern(new Image()));
        }
        overlay.setOpacity(0);
        overlay.setCursor(Cursor.HAND);
        overlay.onMouseEnteredProperty().set(e -> this.onProfileImageMouseEnter(e, overlay));
        overlay.onMouseExitedProperty().set(e -> this.onProfileImageMouseExit(e, overlay));
        overlay.onMouseClickedProperty().set(e -> this.onProfileImageClicked(e));
        avatarContainer.getChildren().addAll(avatar, overlay);
        this.getChildren().add(0, avatarContainer);
    }

    private void onProfileImageMouseEnter(MouseEvent event, Circle overlay) {
        System.out.println("Mouse entered");
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), overlay);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(0.1);
        fadeTransition.setInterpolator(Interpolator.EASE_BOTH);
        fadeTransition.play();
    }

    private void onProfileImageMouseExit(MouseEvent event, Circle overlay) {
        System.out.println("Mouse exited");
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), overlay);
        fadeTransition.setToValue(0.1);
        fadeTransition.setToValue(0);
        fadeTransition.setInterpolator(Interpolator.EASE_BOTH);
        fadeTransition.play();
    }

    private void onProfileImageClicked(MouseEvent event) {
        System.out.println("Mouse clicked");
    }

    @Override
    protected Boolean onPrimaryBtnClicked(MouseEvent event) {
        if (this.validateForm(this.beforeContainer) == false) {
            return false;
        }
        String username = (String) this.textFieldElements.get(USERNAME).getParsedVal();
        String currentPassword = (String) this.textFieldElements.get(CURRENT_PASSWORD).getParsedVal();
        String newPassword = (String) this.textFieldElements.get(NEW_PASSWORD).getParsedVal();
        String fname = (String) this.textFieldElements.get(FNAME).getParsedVal();
        String lname = (String) this.textFieldElements.get(LNAME).getParsedVal();

        try {
            String currentUsername = AppState.getInstance().getUser().getUsername();
            User updatedUser = DB.updateUser(currentUsername, username, currentPassword, fname, lname);
            if (updatedUser == null) {
                this.afterContainer.getChildren().setAll(new AlertView("Something wrong happend!", "error"));
                return false;
            } else {
                this.afterContainer.getChildren()
                                .setAll(new AlertView("User has been successfully created!", "success"));
                AppState.getInstance().setUser(updatedUser);
                Scene dashboardScene = new Scene(new MainScene());
                AppState.getInstance().switchScene(dashboardScene, true);
            }
        } catch (UserNotFoundException e) {
            this.afterContainer.getChildren().setAll(new AlertView("User is not found!", "error"));
            return false;
        }
        return true;
    }
}
